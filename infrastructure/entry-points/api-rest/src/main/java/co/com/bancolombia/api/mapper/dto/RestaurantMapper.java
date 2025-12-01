package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.request.CreateRestaurantRequest;
import co.com.bancolombia.api.dto.response.RestaurantResponse;
import co.com.bancolombia.model.restaurant.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {
    @Mapping(target = "id", ignore = true)
    Restaurant toModel(CreateRestaurantRequest request);
    RestaurantResponse toResponse(Restaurant restaurant);
}
