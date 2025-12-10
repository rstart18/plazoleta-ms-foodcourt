package co.com.bancolombia.usecase.owner;

import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OwnerUseCase implements OwnerService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public boolean validateOwnerRestaurant(Long restaurantId, Long ownerId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        return restaurant != null && restaurant.getOwnerId().equals(ownerId);
    }
}
