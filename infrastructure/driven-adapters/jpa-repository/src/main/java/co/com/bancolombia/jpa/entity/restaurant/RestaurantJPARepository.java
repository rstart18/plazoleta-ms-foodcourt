package co.com.bancolombia.jpa.entity.restaurant;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface RestaurantJPARepository extends CrudRepository<RestaurantEntity, Long>, QueryByExampleExecutor<RestaurantEntity> {
}
