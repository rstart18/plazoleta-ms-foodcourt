package co.com.bancolombia.usecase.createrestaurant;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
public class CreateRestaurantUseCase implements CreateRestaurantService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Restaurant execute(Restaurant restaurant) {
        if (restaurantRepository.existsByNit(restaurant.getNit())) {
            throw new BusinessException(
                    DomainErrorCode.RESTAURANT_NIT_ALREADY_EXISTS.getCode(),
                    DomainErrorCode.RESTAURANT_NIT_ALREADY_EXISTS.getMessage()
            );
        }

        //TODO: consultar si el usuario asignado tiene rol propietario

        return restaurantRepository.create(restaurant);
    }
}
