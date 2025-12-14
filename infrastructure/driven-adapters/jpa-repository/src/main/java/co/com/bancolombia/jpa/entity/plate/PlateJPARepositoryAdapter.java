package co.com.bancolombia.jpa.entity.plate;

import co.com.bancolombia.jpa.entity.restaurant.RestaurantEntity;
import co.com.bancolombia.jpa.helper.AdapterOperations;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public Plate findById(Long id) {
        Optional<PlateEntity> entity = repository.findById(id);
        Plate result = entity.map(plateEntity -> {
            Plate plate = mapper.map(plateEntity, Plate.class);
            if (plateEntity.getRestaurant() != null) {
                plate.setRestaurantId(plateEntity.getRestaurant().getId());
            }
            return plate;
        }).orElse(null);
        
        return result;
    }


    @Override
    public Plate update(Plate plate) {
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

    @Override
    public PagedResult<Plate> findByRestaurantIdAndCategory(Long restaurantId, String category, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PlateEntity> entityPage;
        
        if (category != null && !category.trim().isEmpty()) {
            entityPage = repository.findByRestaurantIdAndCategoryAndActiveTrue(restaurantId, category, pageRequest);
        } else {
            entityPage = repository.findByRestaurantIdAndActiveTrue(restaurantId, pageRequest);
        }
        
        List<Plate> plates = entityPage.getContent().stream()
                .map(entity -> {
                    Plate plate = mapper.map(entity, Plate.class);
                    if (entity.getRestaurant() != null) {
                        plate.setRestaurantId(entity.getRestaurant().getId());
                    }
                    return plate;
                })
                .toList();
        
        return new PagedResult<>(
                plates,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }
}
