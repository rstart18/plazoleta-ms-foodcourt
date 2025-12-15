package co.com.bancolombia.api.dto.response;

import co.com.bancolombia.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderAssignedResponse {
    private Long id;
    private Long clientId;
    private String clientEmail;
    private Long restaurantId;
    private Long employeeId;
    private String employeeEmail;
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}