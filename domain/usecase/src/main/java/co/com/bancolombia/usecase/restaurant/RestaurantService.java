package co.com.bancolombia.usecase.restaurant;

import co.com.bancolombia.model.restaurant.Restaurant;

public interface RestaurantService {
    Restaurant createRestaurant(Restaurant request, String authToken);
}
