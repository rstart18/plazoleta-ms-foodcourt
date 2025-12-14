package co.com.bancolombia.model.order.gateway;

import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.enums.OrderStatus;

import java.util.List;

public interface OrderRepository {
    Order create(Order order);
    Order findById(Long id);
    List<Order> findByCustomerIdAndStatusIn(Long customerId, List<OrderStatus> statuses);
    Order update(Order order);
}
