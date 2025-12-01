package co.com.bancolombia.jpa.entity.restaurant;

import co.com.bancolombia.jpa.helper.AdapterOperations;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RestaurantJPARepositoryAdapter extends AdapterOperations<Restaurant, RestaurantEntity, Long, RestaurantJPARepository>
 implements RestaurantRepository
{

    public RestaurantJPARepositoryAdapter(RestaurantJPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Restaurant.class));
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        return toEntity(repository.save(toData(restaurant)));
    }

    @Override
    public boolean existsByNit(String nit) {
        return repository.existsByNit(nit);
    }

    @Override
    public boolean isOwner(Long restaurantId, Long userId) {
        return repository.existsByIdAndOwnerId(restaurantId, userId);
    }
}
