package co.com.bancolombia.jpa.entity.order;

import co.com.bancolombia.model.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

public interface OrderJPARepository extends CrudRepository<OrderEntity, Long>, QueryByExampleExecutor<OrderEntity> {

    @Query("SELECT o FROM OrderEntity o WHERE o.clientId = :clientId AND o.status IN :statuses")
    List<OrderEntity> findByClientIdAndStatusIn(@Param("clientId") Long clientId,
                                               @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT o FROM OrderEntity o WHERE o.status = :status AND o.restaurantId = :restaurantId")
    Page<OrderEntity> findByStatusAndRestaurantId(@Param("status") OrderStatus status,
                                                  @Param("restaurantId") Long restaurantId,
                                                  Pageable pageable);
}
