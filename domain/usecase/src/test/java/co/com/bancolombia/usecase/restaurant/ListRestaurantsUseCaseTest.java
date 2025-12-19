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

    @Test
    void shouldListRestaurantsOrderedByNameAscending() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(
                Restaurant.builder().id(1L).name("Abc Restaurant").build(),
                Restaurant.builder().id(2L).name("Def Restaurant").build(),
                Restaurant.builder().id(3L).name("Xyz Restaurant").build()
        );
        PagedResult<Restaurant> expectedResult = PagedResult.<Restaurant>builder()
                .content(restaurants)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(3L)
                .totalPages(1)
                .build();
        when(restaurantRepository.findAllOrderByNameAsc(0, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Restaurant> result = restaurantUseCase.listRestaurants(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals("Abc Restaurant", result.getContent().get(0).getName());
        assertEquals("Def Restaurant", result.getContent().get(1).getName());
        assertEquals("Xyz Restaurant", result.getContent().get(2).getName());
        verify(restaurantRepository).findAllOrderByNameAsc(0, 10);
    }

    @Test
    void shouldListRestaurantsWithDifferentPageSize() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(
                Restaurant.builder().id(1L).name("Restaurant A").build(),
                Restaurant.builder().id(2L).name("Restaurant B").build(),
                Restaurant.builder().id(3L).name("Restaurant C").build(),
                Restaurant.builder().id(4L).name("Restaurant D").build(),
                Restaurant.builder().id(5L).name("Restaurant E").build()
        );
        PagedResult<Restaurant> expectedResult = PagedResult.<Restaurant>builder()
                .content(restaurants)
                .pageNumber(0)
                .pageSize(20)
                .totalElements(5L)
                .totalPages(1)
                .build();
        when(restaurantRepository.findAllOrderByNameAsc(0, 20)).thenReturn(expectedResult);

        // When
        PagedResult<Restaurant> result = restaurantUseCase.listRestaurants(0, 20);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getContent().size());
        assertEquals(20, result.getPageSize());
        assertEquals(5L, result.getTotalElements());
        verify(restaurantRepository).findAllOrderByNameAsc(0, 20);
    }

    @Test
    void shouldListRestaurantsWithPaginationSecondPage() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(
                Restaurant.builder().id(11L).name("Restaurant K").build(),
                Restaurant.builder().id(12L).name("Restaurant L").build()
        );
        PagedResult<Restaurant> expectedResult = PagedResult.<Restaurant>builder()
                .content(restaurants)
                .pageNumber(1)
                .pageSize(10)
                .totalElements(12L)
                .totalPages(2)
                .build();
        when(restaurantRepository.findAllOrderByNameAsc(1, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Restaurant> result = restaurantUseCase.listRestaurants(1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertEquals(2, result.getContent().size());
        assertEquals(12L, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        verify(restaurantRepository).findAllOrderByNameAsc(1, 10);
    }

    @Test
    void shouldReturnEmptyListWhenNoRestaurantsFound() {
        // Given
        PagedResult<Restaurant> expectedResult = PagedResult.<Restaurant>builder()
                .content(Arrays.asList())
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0L)
                .totalPages(0)
                .build();
        when(restaurantRepository.findAllOrderByNameAsc(0, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Restaurant> result = restaurantUseCase.listRestaurants(0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        verify(restaurantRepository).findAllOrderByNameAsc(0, 10);
    }

    @Test
    void shouldListRestaurantsWithSingleRestaurant() {
        // Given
        List<Restaurant> restaurants = Arrays.asList(
                Restaurant.builder().id(1L).name("Only Restaurant").build()
        );
        PagedResult<Restaurant> expectedResult = PagedResult.<Restaurant>builder()
                .content(restaurants)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
        when(restaurantRepository.findAllOrderByNameAsc(0, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Restaurant> result = restaurantUseCase.listRestaurants(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(restaurantRepository).findAllOrderByNameAsc(0, 10);
    }
}