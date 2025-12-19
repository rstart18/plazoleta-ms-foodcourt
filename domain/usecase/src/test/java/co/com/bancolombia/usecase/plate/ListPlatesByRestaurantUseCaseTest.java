package co.com.bancolombia.usecase.plate;

import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
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
class ListPlatesByRestaurantUseCaseTest {

    @Mock
    private PlateRepository plateRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private PlateUseCase plateUseCase;

    @Test
    void shouldListPlatesByRestaurantSuccessfully() {
        // Given
        Long restaurantId = 1L;
        String category = "MAIN_COURSE";
        List<Plate> plates = Arrays.asList(
                Plate.builder().id(1L).name("Plate A").restaurantId(restaurantId).build(),
                Plate.builder().id(2L).name("Plate B").restaurantId(restaurantId).build()
        );
        PagedResult<Plate> expectedResult = PagedResult.<Plate>builder()
                .content(plates)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(2L)
                .totalPages(1)
                .build();
        when(plateRepository.findByRestaurantIdAndCategory(restaurantId, category, 0, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Plate> result = plateUseCase.listPlatesByRestaurant(restaurantId, category, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(2L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        verify(plateRepository).findByRestaurantIdAndCategory(restaurantId, category, 0, 10);
    }

    @Test
    void shouldListPlatesByRestaurantWithDifferentPageSize() {
        // Given
        Long restaurantId = 1L;
        String category = "DESSERT";
        List<Plate> plates = Arrays.asList(
                Plate.builder().id(1L).name("Plate A").restaurantId(restaurantId).build(),
                Plate.builder().id(2L).name("Plate B").restaurantId(restaurantId).build(),
                Plate.builder().id(3L).name("Plate C").restaurantId(restaurantId).build()
        );
        PagedResult<Plate> expectedResult = PagedResult.<Plate>builder()
                .content(plates)
                .pageNumber(0)
                .pageSize(20)
                .totalElements(3L)
                .totalPages(1)
                .build();
        when(plateRepository.findByRestaurantIdAndCategory(restaurantId, category, 0, 20)).thenReturn(expectedResult);

        // When
        PagedResult<Plate> result = plateUseCase.listPlatesByRestaurant(restaurantId, category, 0, 20);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(20, result.getPageSize());
        assertEquals(3L, result.getTotalElements());
        verify(plateRepository).findByRestaurantIdAndCategory(restaurantId, category, 0, 20);
    }

    @Test
    void shouldListPlatesByRestaurantWithPaginationSecondPage() {
        // Given
        Long restaurantId = 1L;
        String category = "BEVERAGES";
        List<Plate> plates = Arrays.asList(
                Plate.builder().id(11L).name("Plate K").restaurantId(restaurantId).build(),
                Plate.builder().id(12L).name("Plate L").restaurantId(restaurantId).build()
        );
        PagedResult<Plate> expectedResult = PagedResult.<Plate>builder()
                .content(plates)
                .pageNumber(1)
                .pageSize(10)
                .totalElements(12L)
                .totalPages(2)
                .build();
        when(plateRepository.findByRestaurantIdAndCategory(restaurantId, category, 1, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Plate> result = plateUseCase.listPlatesByRestaurant(restaurantId, category, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPageNumber());
        assertEquals(2, result.getContent().size());
        assertEquals(12L, result.getTotalElements());
        assertEquals(2, result.getTotalPages());
        verify(plateRepository).findByRestaurantIdAndCategory(restaurantId, category, 1, 10);
    }

    @Test
    void shouldReturnEmptyListWhenNoPlatesFound() {
        // Given
        Long restaurantId = 1L;
        String category = "SALADS";
        PagedResult<Plate> expectedResult = PagedResult.<Plate>builder()
                .content(Arrays.asList())
                .pageNumber(0)
                .pageSize(10)
                .totalElements(0L)
                .totalPages(0)
                .build();
        when(plateRepository.findByRestaurantIdAndCategory(restaurantId, category, 0, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Plate> result = plateUseCase.listPlatesByRestaurant(restaurantId, category, 0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        assertEquals(0, result.getTotalPages());
        verify(plateRepository).findByRestaurantIdAndCategory(restaurantId, category, 0, 10);
    }

    @Test
    void shouldListPlatesByRestaurantWithNullCategory() {
        // Given
        Long restaurantId = 1L;
        String category = null;
        List<Plate> plates = Arrays.asList(
                Plate.builder().id(1L).name("Any Plate").restaurantId(restaurantId).build()
        );
        PagedResult<Plate> expectedResult = PagedResult.<Plate>builder()
                .content(plates)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1L)
                .totalPages(1)
                .build();
        when(plateRepository.findByRestaurantIdAndCategory(restaurantId, null, 0, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Plate> result = plateUseCase.listPlatesByRestaurant(restaurantId, category, 0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(plateRepository).findByRestaurantIdAndCategory(restaurantId, null, 0, 10);
    }
}