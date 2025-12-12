package co.com.bancolombia.model.restaurant.gateways;

import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.restaurant.Restaurant;

public interface RestaurantRepository {
    Restaurant create(Restaurant restaurant);
    boolean existsByNit(String nit);
    Restaurant findById(Long id);
    PagedResult<Restaurant> findAllOrderByNameAsc(int pageNumber, int pageSize);
}
