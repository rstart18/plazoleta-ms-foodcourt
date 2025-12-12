package co.com.bancolombia.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlateListResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String urlImage;
    private String category;
}