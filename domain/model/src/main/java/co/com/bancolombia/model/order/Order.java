package co.com.bancolombia.model.order;

import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.orderitem.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder(toBuilder = true)
public class Order {
    private Long id;
    private Long clientId;
    private String clientEmail;
    private String clientPhone;
    private Long restaurantId;
    private Long employeeId;
    private String employeeEmail;
    private List<OrderItem> items;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String securityPin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
