package co.com.bancolombia.usecase.restaurant;

import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.restaurant.Restaurant;

public interface RestaurantService {
    Restaurant createRestaurant(Restaurant request, String authToken);
    PagedResult<Restaurant> listRestaurants(int pageNumber, int pageSize);
}
