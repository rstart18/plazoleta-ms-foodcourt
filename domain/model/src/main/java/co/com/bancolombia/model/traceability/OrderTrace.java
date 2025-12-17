package co.com.bancolombia.model.traceability;

import co.com.bancolombia.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderTrace {
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