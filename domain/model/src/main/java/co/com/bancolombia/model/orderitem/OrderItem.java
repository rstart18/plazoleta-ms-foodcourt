package co.com.bancolombia.model.orderitem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderItem {
    private Long id;
    private Long plateId;
    private String plateName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
