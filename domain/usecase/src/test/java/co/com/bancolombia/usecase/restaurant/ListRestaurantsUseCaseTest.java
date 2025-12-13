package co.com.bancolombia.usecase.restaurant;

import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import co.com.bancolombia.model.user.gateways.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListRestaurantsUseCaseTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private RestaurantUseCase restaurantUseCase;

    @Test
    void shouldListRestaurantsSuccessfully() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(
                Restaurant.builder().id(1L).name("Restaurant A").build(),
                Restaurant.builder().id(2L).name("Restaurant B").build()
        );
        PagedResult<Restaurant> expectedResult = PagedResult.<Restaurant>builder()
                .content(restaurants)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(2L)
                .totalPages(1)
                .build();
        when(restaurantRepository.findAllOrderByNameAsc(0, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Restaurant> result = restaurantUseCase.listRestaurants(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(2L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(restaurantRepository).findAllOrderByNameAsc(0, 10);
    }
}