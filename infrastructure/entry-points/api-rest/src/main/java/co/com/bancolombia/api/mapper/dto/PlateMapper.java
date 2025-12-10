package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.request.CreatePlateRequest;
import co.com.bancolombia.api.dto.request.UpdatePlateRequest;
import co.com.bancolombia.api.dto.response.PlateResponse;
import co.com.bancolombia.api.dto.response.PlateStatusResponse;
import co.com.bancolombia.model.plate.Plate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Plate toModel(CreatePlateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "urlImage", ignore = true)
    @Mapping(target = "restaurantId", ignore = true)
    @Mapping(target = "active", ignore = true)
    Plate toModel(UpdatePlateRequest request);

    PlateResponse toResponse(Plate plate);

    PlateStatusResponse toStatusResponse(Plate plate);
}
