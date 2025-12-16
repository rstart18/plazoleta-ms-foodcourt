package co.com.bancolombia.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderReadyRequest {
    private Long orderId;
}