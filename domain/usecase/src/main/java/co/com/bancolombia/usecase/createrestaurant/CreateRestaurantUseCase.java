package co.com.bancolombia.usecase.createrestaurant;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import co.com.bancolombia.model.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class CreateRestaurantUseCase implements CreateRestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserGateway userGateway;

    @Override
    public Restaurant createRestaurant(Restaurant restaurant, String authToken) {
        if (restaurantRepository.existsByNit(restaurant.getNit())) {
            throw new BusinessException(
                    DomainErrorCode.RESTAURANT_NIT_ALREADY_EXISTS.getCode(),
                    DomainErrorCode.RESTAURANT_NIT_ALREADY_EXISTS.getMessage()
            );
        }

        if (!userGateway.hasOwnerRole(restaurant.getOwnerId(), authToken)) {
            throw new BusinessException(
                    DomainErrorCode.USER_NOT_OWNER.getCode(),
                    DomainErrorCode.USER_NOT_OWNER.getMessage()
            );
        }

        return restaurantRepository.create(restaurant);
    }
}
