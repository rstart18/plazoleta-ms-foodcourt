package co.com.bancolombia.usecase.createplate;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class CreatePlateUseCase implements CreatePlateService {

    private final PlateRepository plateRepository;
    private final RestaurantRepository restaurantRepository;

    @Override
    public Plate execute(Plate plate, Long userId) {

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
}
