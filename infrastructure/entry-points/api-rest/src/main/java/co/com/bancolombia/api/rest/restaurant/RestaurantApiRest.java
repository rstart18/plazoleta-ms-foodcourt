package co.com.bancolombia.api.rest.restaurant;
import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.dto.request.CreateRestaurantRequest;
import co.com.bancolombia.api.dto.response.ApiResponseData;
import co.com.bancolombia.api.dto.response.PageResponse;
import co.com.bancolombia.api.dto.response.RestaurantListResponse;
import co.com.bancolombia.api.dto.response.RestaurantResponse;
import co.com.bancolombia.api.mapper.dto.RestaurantMapper;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.usecase.restaurant.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/restaurants", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RestaurantApiRest {

    private final RestaurantService restaurantService;
    private final RestaurantMapper restaurantMapper;

    @GetMapping
    public ResponseEntity<ApiResponseData<PageResponse<RestaurantListResponse>>> listRestaurants(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {

        PagedResult<Restaurant> restaurants = restaurantService.listRestaurants(pageNumber, pageSize);
        PageResponse<RestaurantListResponse> response = restaurantMapper.toPageResponse(restaurants);

        return ResponseEntity.ok(ApiResponseData.of(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponseData<RestaurantResponse>> createRestaurant(
            @Valid @RequestBody CreateRestaurantRequest request,
            HttpServletRequest httpRequest) {

        String userRole = JwtUserInterceptor.getUserRole(httpRequest);
        Restaurant restaurant = restaurantMapper.toModel(request);
        Restaurant createdRestaurant = restaurantService.createRestaurant(restaurant, userRole);
        RestaurantResponse response = restaurantMapper.toResponse(createdRestaurant);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseData.of(response));
    }
}
