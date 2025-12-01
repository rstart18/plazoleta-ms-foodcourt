package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.request.CreatePlateRequest;
import co.com.bancolombia.api.dto.response.PlateResponse;
import co.com.bancolombia.model.plate.Plate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Plate toModel(CreatePlateRequest request);
    PlateResponse toResponse(Plate plate);
}
