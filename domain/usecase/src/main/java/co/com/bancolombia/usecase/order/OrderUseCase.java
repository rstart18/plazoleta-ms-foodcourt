package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.orderitem.OrderItem;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.traceability.OrderTrace;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import co.com.bancolombia.model.user.gateways.UserGateway;
import co.com.bancolombia.model.notification.gateways.NotificationGateway;
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
    private final NotificationGateway notificationGateway;

    @Override
    public Order createOrder(Order order, Long clientId, String clientEmail, String clientPhone, String userRole) {
        RoleValidator.validateClientRole(userRole);
        OrderValidator.validateOrderStructure(order);
        OrderValidator.validateClientHasNoActiveOrders(clientId, orderRepository);
        OrderValidator.validatePlatesExistAndSameRestaurant(order, plateRepository);

        List<OrderItem> enrichedItems = enrichOrderItems(order.getItems());

        Order orderToCreate = order.toBuilder()
                .clientId(clientId)
                .clientEmail(clientEmail)
                .clientPhone(clientPhone)
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

    @Override
    public Order markOrderAsReady(Long orderId, Long employeeId, String userRole, String authToken) {
        RoleValidator.validateEmployeeRole(userRole);
        
        Order order = orderRepository.findById(orderId);
        OrderValidator.validateOrderExists(order);
        OrderValidator.validateOrderCanBeMarkedAsReady(order);
        
        Long restaurantId = userGateway.getEmployeeRestaurantId(employeeId, authToken);
        OrderValidator.validateOrderBelongsToRestaurant(order, restaurantId);
        
        String securityPin = generateSecurityPin();
        
        Order updatedOrder = order.toBuilder()
                .status(OrderStatus.READY)
                .securityPin(securityPin)
                .updatedAt(LocalDateTime.now())
                .build();
        
        Order result = orderRepository.update(updatedOrder);
        
        traceabilityGateway.sendOrderStatusChange(
                order.getId(),
                order.getClientId(),
                order.getClientEmail(),
                order.getStatus(),
                OrderStatus.READY,
                employeeId,
                order.getEmployeeEmail()
        );
        
        notificationGateway.sendOrderReadySms(order.getClientPhone(), order.getId(), securityPin);
        
        return result;
    }
    
    @Override
    public Order deliverOrder(Long orderId, String securityPin, Long employeeId, String userRole, String authToken) {
        RoleValidator.validateEmployeeRole(userRole);
        
        Order order = orderRepository.findById(orderId);
        OrderValidator.validateOrderExists(order);
        OrderValidator.validateOrderCanBeDelivered(order);
        OrderValidator.validateSecurityPin(order, securityPin);
        
        Long restaurantId = userGateway.getEmployeeRestaurantId(employeeId, authToken);
        OrderValidator.validateOrderBelongsToRestaurant(order, restaurantId);
        
        Order updatedOrder = order.toBuilder()
                .status(OrderStatus.DELIVERED)
                .updatedAt(LocalDateTime.now())
                .build();
        
        Order result = orderRepository.update(updatedOrder);
        
        traceabilityGateway.sendOrderStatusChange(
                order.getId(),
                order.getClientId(),
                order.getClientEmail(),
                order.getStatus(),
                OrderStatus.DELIVERED,
                employeeId,
                order.getEmployeeEmail()
        );
        
        return result;
    }
    
    @Override
    public Order cancelOrder(Long orderId, Long clientId, String userRole) {
        RoleValidator.validateClientRole(userRole);
        
        Order order = orderRepository.findById(orderId);
        OrderValidator.validateOrderExists(order);
        OrderValidator.validateOrderBelongsToClient(order, clientId);
        OrderValidator.validateOrderCanBeCancelled(order);
        
        Order updatedOrder = order.toBuilder()
                .status(OrderStatus.CANCELLED)
                .updatedAt(LocalDateTime.now())
                .build();
        
        Order result = orderRepository.update(updatedOrder);
        
        traceabilityGateway.sendOrderStatusChange(
                order.getId(),
                order.getClientId(),
                order.getClientEmail(),
                order.getStatus(),
                OrderStatus.CANCELLED,
                null,
                null
        );
        
        return result;
    }
    
    private String generateSecurityPin() {
        return String.format("%04d", (int) (Math.random() * 10000));
    }

    @Override
    public List<OrderTrace> getOrderTraces(Long orderId) {
        return traceabilityGateway.getOrderTraces(orderId);
    }
}
