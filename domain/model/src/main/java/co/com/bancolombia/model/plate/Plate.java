package co.com.bancolombia.model.plate;
import co.com.bancolombia.model.restaurant.Restaurant;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
//import lombok.NoArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Plate {
    private Long id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private String urlImage;
    private Long restaurantId;
    private Boolean active;
}
