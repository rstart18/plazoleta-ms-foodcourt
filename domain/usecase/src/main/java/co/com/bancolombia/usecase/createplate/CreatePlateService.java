package co.com.bancolombia.usecase.createplate;

import co.com.bancolombia.model.plate.Plate;

public interface CreatePlateService {
     Plate execute(Plate plate, Long userId);
}
