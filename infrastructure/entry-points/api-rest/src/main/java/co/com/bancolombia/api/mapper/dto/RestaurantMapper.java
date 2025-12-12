package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.request.CreateRestaurantRequest;
import co.com.bancolombia.api.dto.response.PageResponse;
import co.com.bancolombia.api.dto.response.RestaurantListResponse;
import co.com.bancolombia.api.dto.response.RestaurantResponse;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    @Mapping(target = "id", ignore = true)
    Restaurant toModel(CreateRestaurantRequest request);
    RestaurantResponse toResponse(Restaurant restaurant);

    @Mapping(target = "content", source = "content")
    @Mapping(target = "pageNumber", source = "pageNumber")
    @Mapping(target = "pageSize", source = "pageSize")
    @Mapping(target = "totalElements", source = "totalElements")
    @Mapping(target = "totalPages", source = "totalPages")
    PageResponse<RestaurantListResponse> toPageResponse(PagedResult<Restaurant> page);
}
