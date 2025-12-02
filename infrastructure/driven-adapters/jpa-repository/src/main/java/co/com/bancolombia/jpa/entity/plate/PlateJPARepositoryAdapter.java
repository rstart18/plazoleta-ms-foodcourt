package co.com.bancolombia.jpa.entity.plate;

import co.com.bancolombia.jpa.entity.restaurant.RestaurantEntity;
import co.com.bancolombia.jpa.helper.AdapterOperations;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PlateJPARepositoryAdapter extends AdapterOperations<Plate, PlateEntity, Long, PlateJPARepository>
 implements PlateRepository
{

    public PlateJPARepositoryAdapter(PlateJPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Plate.class));
    }

    @Override
    public Plate create(Plate plate) {
        PlateEntity entity = mapper.map(plate, PlateEntity.class);

        if (plate.getRestaurantId() != null) {
            RestaurantEntity restaurantEntity = new RestaurantEntity();
            restaurantEntity.setId(plate.getRestaurantId());
            entity.setRestaurant(restaurantEntity);
        }

        PlateEntity savedEntity = repository.save(entity);

        Plate result = mapper.map(savedEntity, Plate.class);
        if (savedEntity.getRestaurant() != null) {
            result.setRestaurantId(savedEntity.getRestaurant().getId());
        }

        return result;
    }
}
