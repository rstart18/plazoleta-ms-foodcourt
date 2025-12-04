package co.com.bancolombia.usecase.updatePlate;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePlateUseCaseTest {

    @Mock
    private PlateRepository plateRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private UpdatePlateUseCase updatePlateUseCase;

    private Plate existingPlate;
    private Plate plateUpdates;
    private Restaurant restaurant;
    private Long plateId;
    private Long userId;

    @BeforeEach
    void setUp() {
        plateId = 1L;
        userId = 1L;

        existingPlate = Plate.builder()
                .id(plateId)
                .name("Shawarma Original")
                .price(new BigDecimal("25000.00"))
                .description("Descripción original")
                .urlImage("https://example.com/images/shawarma.jpg")
                .category("Shawarma")
                .restaurantId(1L)
                .active(true)
                .build();

        plateUpdates = Plate.builder()
                .price(new BigDecimal("28000.00"))
                .description("Tortilla, vegetales frescos, carne premium, pollo marinado, salsa de ajo especial")
                .build();

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .ownerId(userId)
                .build();
    }

    @Test
    void shouldUpdatePlateSuccessfully() {
        // Given
        Plate expectedUpdatedPlate = existingPlate.toBuilder()
                .price(new BigDecimal("28000.00"))
                .description("Tortilla, vegetales frescos, carne premium, pollo marinado, salsa de ajo especial")
                .build();

        when(plateRepository.findById(plateId)).thenReturn(existingPlate);
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);
        when(plateRepository.update(any(Plate.class))).thenReturn(expectedUpdatedPlate);

        // When
        Plate result = updatePlateUseCase.updatePlate(plateId, plateUpdates, userId);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("28000.00"), result.getPrice());
        assertEquals("Tortilla, vegetales frescos, carne premium, pollo marinado, salsa de ajo especial", result.getDescription());
        assertEquals("Shawarma Original", result.getName()); // No debe cambiar
        assertEquals("Shawarma", result.getCategory()); // No debe cambiar

        verify(plateRepository).findById(plateId);
        verify(restaurantRepository).findById(1L);
        verify(plateRepository).update(any(Plate.class));
    }

    @Test
    void shouldThrowExceptionWhenPlateNotFound() {
        // Given
        when(plateRepository.findById(plateId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> updatePlateUseCase.updatePlate(plateId, plateUpdates, userId));

        assertEquals(DomainErrorCode.PLATE_NOT_FOUND.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.PLATE_NOT_FOUND.getMessage(), exception.getMessage());

        verify(plateRepository).findById(plateId);
        verify(restaurantRepository, never()).findById(any());
        verify(plateRepository, never()).update(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotRestaurantOwner() {
        // Given
        Long differentUserId = 2L;
        when(plateRepository.findById(plateId)).thenReturn(existingPlate);
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> updatePlateUseCase.updatePlate(plateId, plateUpdates, differentUserId));

        assertEquals(DomainErrorCode.RESTAURANT_NOT_OWNER.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.RESTAURANT_NOT_OWNER.getMessage(), exception.getMessage());

        verify(plateRepository).findById(plateId);
        verify(restaurantRepository).findById(1L);
        verify(plateRepository, never()).update(any());
    }

    @Test
    void shouldOnlyUpdatePriceAndDescription() {
        // Given
        when(plateRepository.findById(plateId)).thenReturn(existingPlate);
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);
        when(plateRepository.update(any(Plate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Plate result = updatePlateUseCase.updatePlate(plateId, plateUpdates, userId);

        // Then
        assertEquals(new BigDecimal("28000.00"), result.getPrice());
        assertEquals("Tortilla, vegetales frescos, carne premium, pollo marinado, salsa de ajo especial", result.getDescription());

        assertEquals(existingPlate.getName(), result.getName());
        assertEquals(existingPlate.getCategory(), result.getCategory());
        assertEquals(existingPlate.getUrlImage(), result.getUrlImage());
        assertEquals(existingPlate.getRestaurantId(), result.getRestaurantId());
        assertEquals(existingPlate.getActive(), result.getActive());
    }
}
