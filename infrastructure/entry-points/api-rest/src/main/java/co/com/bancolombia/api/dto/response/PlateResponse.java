package co.com.bancolombia.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PlateResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private String urlImage;
    private String category;
    private Long restaurantId;
    private Boolean active;
}
