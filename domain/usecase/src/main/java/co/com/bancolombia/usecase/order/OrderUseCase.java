package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.orderitem.OrderItem;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.usecase.validator.OrderValidator;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class OrderUseCase implements OrderService {

    private final OrderRepository orderRepository;
    private final PlateRepository plateRepository;

    @Override
    public Order createOrder(Order order, Long customerId) {
        OrderValidator.validateOrderStructure(order);
        OrderValidator.validateCustomerHasNoActiveOrders(customerId, orderRepository);
        OrderValidator.validatePlatesExistAndSameRestaurant(order, plateRepository);

        List<OrderItem> enrichedItems = enrichOrderItems(order.getItems());

        Order orderToCreate = order.toBuilder()
                .customerId(customerId)
                .status(OrderStatus.PENDING)
                .items(enrichedItems)
                .totalAmount(calculateTotalAmount(enrichedItems))
                .createdAt(LocalDateTime.now())
                .build();

        return orderRepository.create(orderToCreate);
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
}
