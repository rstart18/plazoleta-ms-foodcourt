package co.com.bancolombia.jpa.entity.restaurant;

import co.com.bancolombia.jpa.helper.AdapterOperations;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public PagedResult<Restaurant> findAllOrderByNameAsc(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<RestaurantEntity> springPage = repository.findAllByOrderByNameAsc(pageable);

        return PagedResult.<Restaurant>builder()
                .content(springPage.getContent().stream().map(this::toEntity).toList())
                .pageNumber(springPage.getNumber())
                .pageSize(springPage.getSize())
                .totalElements(springPage.getTotalElements())
                .totalPages(springPage.getTotalPages())
                .build();
    }
}
