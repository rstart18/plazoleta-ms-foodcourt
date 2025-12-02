package co.com.bancolombia.usecase.createrestaurant;

import co.com.bancolombia.model.restaurant.Restaurant;

public interface CreateRestaurantService {
    Restaurant createRestaurant(Restaurant request, String authToken);
}
