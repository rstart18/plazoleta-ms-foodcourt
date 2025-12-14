package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.enums.OrderStatus;

public interface OrderService {
    Order createOrder(Order order, Long customerId, String userRole);
    PagedResult<Order> listOrdersByStatus(OrderStatus status, int page, int size, Long employeeId, String userRole, String authToken);
    Order assignOrderToEmployee(Long orderId, Long employeeId, String userRole, String authToken);
}
