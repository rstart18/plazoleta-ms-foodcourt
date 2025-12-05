package co.com.bancolombia.usecase.validateowner;

public interface ValidateOwnerService {
    boolean validateOwnerRestaurant(Long restaurantId, Long ownerId);
}
