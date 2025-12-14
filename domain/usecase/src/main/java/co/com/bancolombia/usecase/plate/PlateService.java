package co.com.bancolombia.usecase.plate;

import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.plate.Plate;

public interface PlateService {
    Plate createPlate(Plate plate, Long userId, String userRole);
    Plate updatePlate(Long plateId, Plate plateUpdates, Long userId, String userRole);
    Plate togglePlateStatus(Long plateId, Long userId, String userRole);
    PagedResult<Plate> listPlatesByRestaurant(Long restaurantId, String category, int page, int size);
}
