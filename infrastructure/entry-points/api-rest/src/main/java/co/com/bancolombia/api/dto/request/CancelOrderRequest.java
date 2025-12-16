package co.com.bancolombia.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CancelOrderRequest {
    @NotNull
    private Long orderId;
}