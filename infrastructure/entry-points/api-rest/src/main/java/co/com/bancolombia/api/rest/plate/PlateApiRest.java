package co.com.bancolombia.api.rest.plate;
import co.com.bancolombia.api.constants.SecurityConstants;
import co.com.bancolombia.api.dto.request.CreatePlateRequest;
import co.com.bancolombia.api.dto.response.ApiResponse;
import co.com.bancolombia.api.dto.response.PlateResponse;
import co.com.bancolombia.api.mapper.dto.PlateMapper;
import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.usecase.createplate.CreatePlateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/plates", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PlateApiRest {

    private final CreatePlateService createPlateService;
    private final PlateMapper plateMapper;

    @PostMapping
    @PreAuthorize(SecurityConstants.ROLE_OWNER)
    public ResponseEntity<ApiResponse<PlateResponse>> createPlate(
            @Valid @RequestBody CreatePlateRequest request,
            Authentication authentication) {
        Plate plate = plateMapper.toModel(request);

        Long userId = getUserIdFromToken(authentication);
        Plate createdPlate = createPlateService.execute(plate, userId);

        PlateResponse response = plateMapper.toResponse(createdPlate);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    private Long getUserIdFromToken(Authentication authentication) {
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;

        Number userId = jwtToken.getToken().getClaim("userId");

        if (userId == null) {
            throw new BusinessException(
                    DomainErrorCode.INVALID_TOKEN.getCode(),
                    "Token JWT no contiene userId"
            );
        }

        return userId.longValue();
    }

}
