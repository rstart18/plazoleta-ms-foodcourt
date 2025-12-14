package co.com.bancolombia.api.rest.restaurant;

import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.dto.response.ApiResponse;
import co.com.bancolombia.api.dto.response.OwnerValidationResponse;
import co.com.bancolombia.usecase.owner.OwnerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RestaurantValidationApiRest {

    private final OwnerService validateOwnerService;

    @GetMapping("/{restaurantId}/owner/{ownerId}")
    public ResponseEntity<ApiResponse<OwnerValidationResponse>> validateOwnerRestaurant(
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("ownerId") Long ownerId,
            HttpServletRequest request) {

        String userRole = JwtUserInterceptor.getUserRole(request);
        boolean isOwner = validateOwnerService.validateOwnerRestaurant(restaurantId, ownerId, userRole);
        OwnerValidationResponse response = new OwnerValidationResponse(isOwner);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
