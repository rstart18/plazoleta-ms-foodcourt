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
}