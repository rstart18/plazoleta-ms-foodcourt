package co.com.bancolombia.usecase.owner;

public interface OwnerService {
    boolean validateOwnerRestaurant(Long restaurantId, Long ownerId);
}
