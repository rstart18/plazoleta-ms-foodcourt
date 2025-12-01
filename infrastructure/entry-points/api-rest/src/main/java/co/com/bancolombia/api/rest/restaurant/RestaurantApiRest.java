package co.com.bancolombia.api.rest.restaurant;
import co.com.bancolombia.api.constants.SecurityConstants;
import co.com.bancolombia.api.dto.request.CreateRestaurantRequest;
import co.com.bancolombia.api.dto.response.ApiResponse;
import co.com.bancolombia.api.dto.response.RestaurantResponse;
import co.com.bancolombia.api.mapper.dto.RestaurantMapper;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.usecase.createrestaurant.CreateRestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RestaurantApiRest {

    private final CreateRestaurantService createRestaurantService;
    private final RestaurantMapper restaurantMapper;

    @PostMapping
    @PreAuthorize(SecurityConstants.ROLE_ADMIN)
    public ResponseEntity<ApiResponse<RestaurantResponse>> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request) {

        Restaurant restaurant = restaurantMapper.toModel(request);
        Restaurant createdRestaurant = createRestaurantService.execute(restaurant);
        RestaurantResponse response = restaurantMapper.toResponse(createdRestaurant);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }
}
