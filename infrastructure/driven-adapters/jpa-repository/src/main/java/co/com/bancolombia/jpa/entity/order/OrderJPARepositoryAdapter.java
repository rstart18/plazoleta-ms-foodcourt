package co.com.bancolombia.jpa.entity.order;

import co.com.bancolombia.jpa.helper.AdapterOperations;
import co.com.bancolombia.jpa.orderitem.OrderItemEntity;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.orderitem.OrderItem;
import co.com.bancolombia.model.page.PagedResult;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OrderJPARepositoryAdapter extends AdapterOperations<Order, OrderEntity, Long, OrderJPARepository>
        implements OrderRepository {

    public OrderJPARepositoryAdapter(OrderJPARepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Order.class));
    }

    @Override
    public Order create(Order order) {
        OrderEntity entity = mapper.map(order, OrderEntity.class);

        if (order.getItems() != null) {
            List<OrderItemEntity> itemEntities = order.getItems().stream()
                    .map(item -> mapToOrderItemEntity(item, entity))
                    .toList();
            entity.setItems(itemEntities);
        }

        OrderEntity savedEntity = repository.save(entity);
        return mapToOrder(savedEntity);
    }

    private OrderItemEntity mapToOrderItemEntity(OrderItem item, OrderEntity orderEntity) {
        return OrderItemEntity.builder()
                .id(item.getId())
                .order(orderEntity)
                .plateId(item.getPlateId())
                .plateName(item.getPlateName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }

    @Override
    public Order findById(Long id) {
        Optional<OrderEntity> entity = repository.findById(id);
        return entity.map(this::mapToOrder).orElse(null);
    }

    @Override
    public List<Order> findByCustomerIdAndStatusIn(Long customerId, List<OrderStatus> statuses) {
        return repository.findByCustomerIdAndStatusIn(customerId, statuses)
                .stream()
                .map(this::mapToOrder)
                .toList();
    }

    @Override
    public Order update(Order order) {
        OrderEntity entity = mapper.map(order, OrderEntity.class);

        if (order.getItems() != null) {
            List<OrderItemEntity> itemEntities = order.getItems().stream()
                    .map(item -> {
                        OrderItemEntity itemEntity = mapper.map(item, OrderItemEntity.class);
                        itemEntity.setOrder(entity);
                        return itemEntity;
                    })
                    .toList();
            entity.setItems(itemEntities);
        }

        OrderEntity savedEntity = repository.save(entity);
        return mapToOrder(savedEntity);
    }

    private Order mapToOrder(OrderEntity entity) {
        Order order = mapper.map(entity, Order.class);

        if (entity.getItems() != null) {
            List<OrderItem> items = entity.getItems().stream()
                    .map(this::mapToOrderItem)
                    .toList();
            order.setItems(items);
        }

        return order;
    }

    private OrderItem mapToOrderItem(OrderItemEntity entity) {
        return OrderItem.builder()
                .id(entity.getId())
                .plateId(entity.getPlateId())
                .plateName(entity.getPlateName())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
                .build();
    }

    @Override
    public PagedResult<Order> findByStatusAndRestaurantId(OrderStatus status, Long restaurantId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OrderEntity> entityPage = repository.findByStatusAndRestaurantId(status, restaurantId, pageRequest);
        
        List<Order> orders = entityPage.getContent().stream()
                .map(this::mapToOrder)
                .toList();
        
        return new PagedResult<>(
                orders,
                entityPage.getNumber(),
                entityPage.getSize(),
                entityPage.getTotalElements(),
                entityPage.getTotalPages()
        );
    }
}
