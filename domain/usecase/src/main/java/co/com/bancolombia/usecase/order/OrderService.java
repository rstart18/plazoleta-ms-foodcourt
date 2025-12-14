package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.order.Order;

public interface OrderService {
    Order createOrder(Order order, Long customerId);
}
