package co.com.bancolombia.usecase.validateowner;

import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidateOwnerUseCase implements ValidateOwnerService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public boolean validateOwnerRestaurant(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        return restaurant != null && restaurant.getOwnerId().equals(ownerId);
    }
}
