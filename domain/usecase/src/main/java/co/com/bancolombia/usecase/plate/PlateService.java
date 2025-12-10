package co.com.bancolombia.usecase.plate;

import co.com.bancolombia.model.plate.Plate;

public interface PlateService {
    Plate createPlate(Plate plate, Long userId);
    Plate updatePlate(Long plateId, Plate plateUpdates, Long userId);
}
