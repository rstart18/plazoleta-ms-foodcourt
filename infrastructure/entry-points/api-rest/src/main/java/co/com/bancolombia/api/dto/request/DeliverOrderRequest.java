package co.com.bancolombia.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliverOrderRequest {
    @NotNull
    private Long orderId;
    
    @NotBlank
    private String securityPin;
}