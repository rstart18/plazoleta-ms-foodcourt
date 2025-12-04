package co.com.bancolombia.api.rest.plate;
import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.constants.SecurityConstants;
import co.com.bancolombia.api.dto.request.CreatePlateRequest;
import co.com.bancolombia.api.dto.request.UpdatePlateRequest;
import co.com.bancolombia.api.dto.response.ApiResponse;
import co.com.bancolombia.api.dto.response.PlateResponse;
import co.com.bancolombia.api.mapper.dto.PlateMapper;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.usecase.createplate.CreatePlateService;
import co.com.bancolombia.usecase.updatePlate.UpdatePlateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/plates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PlateApiRest {

    private final CreatePlateService createPlateService;
    private final UpdatePlateService updatePlateService;
    private final PlateMapper plateMapper;

    @PostMapping
    @PreAuthorize(SecurityConstants.ROLE_OWNER)
    public ResponseEntity<ApiResponse<PlateResponse>> createPlate(
            @Valid @RequestBody CreatePlateRequest request,
            HttpServletRequest httpRequest) {
        Plate plate = plateMapper.toModel(request);

        Long userId = JwtUserInterceptor.getUserId(httpRequest);
        Plate createdPlate = createPlateService.createPlate(plate, userId);

        PlateResponse response = plateMapper.toResponse(createdPlate);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @PatchMapping("/{plateId}")
    @PreAuthorize(SecurityConstants.ROLE_OWNER)
    public ResponseEntity<ApiResponse<PlateResponse>> updatePlate(
            @PathVariable("plateId") Long plateId,
            @Valid @RequestBody UpdatePlateRequest request,
            HttpServletRequest httpRequest) {

        Plate plateUpdates = plateMapper.toModel(request);
        Long userId = JwtUserInterceptor.getUserId(httpRequest);
        Plate updatedPlate = updatePlateService.updatePlate(plateId, plateUpdates, userId);

        PlateResponse response = plateMapper.toResponse(updatedPlate);
        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
