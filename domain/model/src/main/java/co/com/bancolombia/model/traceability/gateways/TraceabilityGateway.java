package co.com.bancolombia.model.traceability.gateways;

import co.com.bancolombia.model.enums.OrderStatus;

public interface TraceabilityGateway {
    void sendOrderStatusChange(Long orderId, Long clientId, String clientEmail, 
                              OrderStatus previousStatus, OrderStatus newStatus, 
                              Long employeeId, String employeeEmail);
}