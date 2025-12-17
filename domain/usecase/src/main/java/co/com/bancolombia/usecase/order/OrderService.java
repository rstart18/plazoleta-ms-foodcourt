package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.traceability.OrderTrace;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order, Long clientId, String clientEmail, String clientPhone, String userRole);
    PagedResult<Order> listOrdersByStatus(OrderStatus status, int page, int size, Long employeeId, String userRole, String authToken);
    Order assignOrderToEmployee(Long orderId, Long employeeId, String employeeEmail, String userRole, String authToken);
    Order markOrderAsReady(Long orderId, Long employeeId, String userRole, String authToken);
    Order deliverOrder(Long orderId, String securityPin, Long employeeId, String userRole, String authToken);
    Order cancelOrder(Long orderId, Long clientId, String userRole);
    List<OrderTrace> getOrderTraces(Long orderId);
}
