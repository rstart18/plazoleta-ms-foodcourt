package co.com.bancolombia.usecase.createplate;

import co.com.bancolombia.model.plate.Plate;

public interface CreatePlateService {
     Plate createPlate(Plate plate, Long userId);
}
