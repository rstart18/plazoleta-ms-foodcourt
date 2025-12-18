package co.com.bancolombia.model.traceability.gateways;

import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.traceability.EmployeeRanking;
import co.com.bancolombia.model.traceability.OrderTrace;

import java.util.List;

public interface TraceabilityGateway {
    void sendOrderStatusChange(Long orderId, Long clientId, String clientEmail,
                              OrderStatus previousStatus, OrderStatus newStatus,
                              Long employeeId, String employeeEmail);

    List<OrderTrace> getOrderTraces(Long orderId);

    List<EmployeeRanking> getEmployeesRanking();
}