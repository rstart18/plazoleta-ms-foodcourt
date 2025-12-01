package co.com.bancolombia.jpa.entity.plate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface PlateJPARepository extends CrudRepository<PlateEntity, Long>, QueryByExampleExecutor<PlateEntity> {
}
