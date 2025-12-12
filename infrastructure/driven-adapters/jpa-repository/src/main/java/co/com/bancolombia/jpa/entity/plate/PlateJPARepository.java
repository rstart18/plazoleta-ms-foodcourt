package co.com.bancolombia.jpa.entity.plate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface PlateJPARepository extends CrudRepository<PlateEntity, Long>, QueryByExampleExecutor<PlateEntity> {
    Page<PlateEntity> findByRestaurantIdAndActiveTrue(Long restaurantId, Pageable pageable);
    Page<PlateEntity> findByRestaurantIdAndCategoryAndActiveTrue(Long restaurantId, String category, Pageable pageable);
}
