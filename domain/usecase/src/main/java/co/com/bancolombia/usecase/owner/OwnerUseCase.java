package co.com.bancolombia.usecase.owner;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import co.com.bancolombia.usecase.validator.RoleValidator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OwnerUseCase implements OwnerService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public boolean validateOwnerRestaurant(Long restaurantId, Long ownerId, String userRole) {
        RoleValidator.validateOwnerRole(userRole);
        
        Restaurant restaurant = restaurantRepository.findById(restaurantId);
        if (restaurant == null) {
            throw new BusinessException(
                    DomainErrorCode.RESTAURANT_NOT_FOUND.getCode(),
                    DomainErrorCode.RESTAURANT_NOT_FOUND.getMessage()
            );
        }
        
        return restaurant.getOwnerId().equals(ownerId);
    }


}
