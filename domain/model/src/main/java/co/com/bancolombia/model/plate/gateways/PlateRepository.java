package co.com.bancolombia.model.plate.gateways;

import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.plate.Plate;

public interface PlateRepository {
    Plate create(Plate plate);
    Plate findById(Long id);
    Plate update(Plate plate);
    PagedResult<Plate> findByRestaurantIdAndCategory(Long restaurantId, String category, int page, int size);
}
