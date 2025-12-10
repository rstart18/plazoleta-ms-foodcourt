package co.com.bancolombia.usecase.plate;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlateUseCase implements PlateService {

    private final PlateRepository plateRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public Plate createPlate(Plate plate, Long userId) {

        Restaurant restaurant = restaurantRepository.findById(plate.getRestaurantId());
        if (restaurant == null) {
            throw new BusinessException(
                    DomainErrorCode.RESTAURANT_NOT_FOUND.getCode(),
                    DomainErrorCode.RESTAURANT_NOT_FOUND.getMessage()
            );
        }

        if (!restaurant.getOwnerId().equals(userId)) {
            throw new BusinessException(
                    DomainErrorCode.RESTAURANT_NOT_OWNER.getCode(),
                    DomainErrorCode.RESTAURANT_NOT_OWNER.getMessage()
            );
        }

        return plateRepository.create(plate);
    }

    @Override
    public Plate updatePlate(Long plateId, Plate plateUpdates, Long userId) {
        Plate existingPlate = plateRepository.findById(plateId);
        if (existingPlate == null) {
            throw new BusinessException(
                    DomainErrorCode.PLATE_NOT_FOUND.getCode(),
                    DomainErrorCode.PLATE_NOT_FOUND.getMessage()
            );
        }

        Restaurant restaurant = restaurantRepository.findById(existingPlate.getRestaurantId());
        if (!restaurant.getOwnerId().equals(userId)) {
            throw new BusinessException(
                    DomainErrorCode.RESTAURANT_NOT_OWNER.getCode(),
                    DomainErrorCode.RESTAURANT_NOT_OWNER.getMessage()
            );
        }

        Plate updatedPlate = existingPlate.toBuilder()
                .price(plateUpdates.getPrice())
                .description(plateUpdates.getDescription())
                .build();

        return plateRepository.update(updatedPlate);
    }
}
