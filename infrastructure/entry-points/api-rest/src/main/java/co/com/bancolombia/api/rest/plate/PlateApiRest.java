package co.com.bancolombia.api.rest.plate;

import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.dto.request.CreatePlateRequest;
import co.com.bancolombia.api.dto.request.UpdatePlateRequest;
import co.com.bancolombia.api.dto.response.ApiResponseData;
import co.com.bancolombia.api.dto.response.PagedPlateResponse;
import co.com.bancolombia.api.dto.response.PlateResponse;
import co.com.bancolombia.api.dto.response.PlateListResponse;
import co.com.bancolombia.api.dto.response.PlateStatusResponse;
import co.com.bancolombia.api.mapper.dto.PlateMapper;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.usecase.plate.PlateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/plates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PlateApiRest {

    private final PlateService plateService;
    private final PlateMapper plateMapper;

    @PostMapping
    public ResponseEntity<ApiResponseData<PlateResponse>> createPlate(
            @Valid @RequestBody CreatePlateRequest request,
            HttpServletRequest httpRequest) {
        Plate plate = plateMapper.toModel(request);

        Long userId = JwtUserInterceptor.getUserId(httpRequest);
        String userRole = JwtUserInterceptor.getUserRole(httpRequest);
        Plate createdPlate = plateService.createPlate(plate, userId, userRole);

        PlateResponse response = plateMapper.toResponse(createdPlate);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseData.of(response));
    }

    @PatchMapping("/{plateId}")
    public ResponseEntity<ApiResponseData<PlateResponse>> updatePlate(
            @PathVariable("plateId") Long plateId,
            @Valid @RequestBody UpdatePlateRequest request,
            HttpServletRequest httpRequest) {

        Plate plateUpdates = plateMapper.toModel(request);
        Long userId = JwtUserInterceptor.getUserId(httpRequest);
        String userRole = JwtUserInterceptor.getUserRole(httpRequest);
        Plate updatedPlate = plateService.updatePlate(plateId, plateUpdates, userId, userRole);

        PlateResponse response = plateMapper.toResponse(updatedPlate);
        return ResponseEntity.ok(ApiResponseData.of(response));
    }

    @PatchMapping("/{plateId}/status")
    public ResponseEntity<ApiResponseData<PlateStatusResponse>> togglePlateStatus(
            @PathVariable("plateId") Long plateId,
            HttpServletRequest httpRequest) {

        Long userId = JwtUserInterceptor.getUserId(httpRequest);
        String userRole = JwtUserInterceptor.getUserRole(httpRequest);
        Plate updatedPlate = plateService.togglePlateStatus(plateId, userId, userRole);

        PlateStatusResponse response = plateMapper.toStatusResponse(updatedPlate);
        return ResponseEntity.ok(ApiResponseData.of(response));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponseData<PagedPlateResponse>> getPlatesByRestaurant(
            @PathVariable("restaurantId") Long restaurantId,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        PagedResult<Plate> pagedResult = plateService.listPlatesByRestaurant(restaurantId, category, page, size);
        
        List<PlateListResponse> plates = pagedResult.getContent().stream()
                .map(plate -> new PlateListResponse(
                        plate.getId(),
                        plate.getName(),
                        plate.getDescription(),
                        plate.getPrice(),
                        plate.getUrlImage(),
                        plate.getCategory()))
                .toList();
        
        PagedPlateResponse response = new PagedPlateResponse(
                plates,
                pagedResult.getPageNumber(),
                pagedResult.getPageSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages()
        );
        
        return ResponseEntity.ok(ApiResponseData.of(response));
    }

}
