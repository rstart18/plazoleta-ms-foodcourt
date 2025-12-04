package co.com.bancolombia.usecase.updatePlate;

import co.com.bancolombia.model.plate.Plate;

public interface UpdatePlateService {
    Plate updatePlate(Long plateId, Plate plateUpdates, Long userId);
}
