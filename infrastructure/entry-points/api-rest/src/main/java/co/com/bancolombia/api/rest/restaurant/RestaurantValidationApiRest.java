package co.com.bancolombia.api.rest.restaurant;

import co.com.bancolombia.api.constants.SecurityConstants;
import co.com.bancolombia.api.dto.response.ApiResponse;
import co.com.bancolombia.api.dto.response.OwnerValidationResponse;
import co.com.bancolombia.usecase.owner.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize(SecurityConstants.ROLE_OWNER)
    public ResponseEntity<ApiResponse<OwnerValidationResponse>> validateOwnerRestaurant(
            @PathVariable("restaurantId") Long restaurantId,
            @PathVariable("ownerId") Long ownerId) {

        boolean isOwner = validateOwnerService.validateOwnerRestaurant(restaurantId, ownerId);
        OwnerValidationResponse response = new OwnerValidationResponse(isOwner);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}
