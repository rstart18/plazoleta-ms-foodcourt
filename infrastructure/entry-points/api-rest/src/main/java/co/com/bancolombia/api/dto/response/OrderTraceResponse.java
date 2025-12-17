package co.com.bancolombia.api.dto.response;

import co.com.bancolombia.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderTraceResponse {
    private Long id;
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private OrderStatus previousStatus;
    private OrderStatus newStatus;
    private Long employeeId;
    private String employeeEmail;
    private LocalDateTime timestamp;
}