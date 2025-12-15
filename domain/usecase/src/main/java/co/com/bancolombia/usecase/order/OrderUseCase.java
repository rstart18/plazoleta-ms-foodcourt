package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.orderitem.OrderItem;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import co.com.bancolombia.model.user.gateways.UserGateway;
import co.com.bancolombia.usecase.validator.OrderValidator;
import co.com.bancolombia.usecase.validator.RoleValidator;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class OrderUseCase implements OrderService {

    private final OrderRepository orderRepository;
    private final PlateRepository plateRepository;
    private final UserGateway userGateway;
    private final TraceabilityGateway traceabilityGateway;

    @Override
    public Order createOrder(Order order, Long clientId, String clientEmail, String userRole) {
        RoleValidator.validateClientRole(userRole);
        OrderValidator.validateOrderStructure(order);
        OrderValidator.validateClientHasNoActiveOrders(clientId, orderRepository);
        OrderValidator.validatePlatesExistAndSameRestaurant(order, plateRepository);

        List<OrderItem> enrichedItems = enrichOrderItems(order.getItems());

        Order orderToCreate = order.toBuilder()
                .clientId(clientId)
                .clientEmail(clientEmail)
                .status(OrderStatus.PENDING)
                .items(enrichedItems)
                .totalAmount(calculateTotalAmount(enrichedItems))
                .createdAt(LocalDateTime.now())
                .build();

        Order createdOrder = orderRepository.create(orderToCreate);
        
        traceabilityGateway.sendOrderStatusChange(
                createdOrder.getId(),
                createdOrder.getClientId(),
                createdOrder.getClientEmail(),
                null,
                OrderStatus.PENDING,
                null,
                null
        );

        return createdOrder;
    }

    private List<OrderItem> enrichOrderItems(List<OrderItem> items) {
        return items.stream()
                .map(this::enrichOrderItem)
                .toList();
    }

    private OrderItem enrichOrderItem(OrderItem item) {
        Plate plate = plateRepository.findById(item.getPlateId());
        BigDecimal subtotal = plate.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        return item.toBuilder()
                .plateName(plate.getName())
                .unitPrice(plate.getPrice())
                .subtotal(subtotal)
                .build();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public PagedResult<Order> listOrdersByStatus(OrderStatus status, int page, int size, Long employeeId, String userRole, String authToken) {
        RoleValidator.validateEmployeeRole(userRole);
        
        Long restaurantId = userGateway.getEmployeeRestaurantId(employeeId, authToken);
        return orderRepository.findByStatusAndRestaurantId(status, restaurantId, page, size);
    }

    @Override
    public Order assignOrderToEmployee(Long orderId, Long employeeId, String employeeEmail, String userRole, String authToken) {
        RoleValidator.validateEmployeeRole(userRole);
        
        Order order = orderRepository.findById(orderId);
        OrderValidator.validateOrderExists(order);
        OrderValidator.validateOrderCanBeAssigned(order);
        
        Long restaurantId = userGateway.getEmployeeRestaurantId(employeeId, authToken);
        OrderValidator.validateOrderBelongsToRestaurant(order, restaurantId);
        
        Order updatedOrder = order.toBuilder()
                .employeeId(employeeId)
                .employeeEmail(employeeEmail)
                .status(OrderStatus.IN_PREPARATION)
                .updatedAt(LocalDateTime.now())
                .build();
        
        Order result = orderRepository.update(updatedOrder);
        
        traceabilityGateway.sendOrderStatusChange(
                order.getId(),
                order.getClientId(),
                order.getClientEmail(),
                order.getStatus(),
                OrderStatus.IN_PREPARATION,
                employeeId,
                employeeEmail
        );
        
        return result;
    }
}
