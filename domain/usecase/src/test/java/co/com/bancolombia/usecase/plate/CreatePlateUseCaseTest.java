package co.com.bancolombia.usecase.plate;

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
class CreatePlateUseCaseTest {

    @Mock
    private PlateRepository plateRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private PlateUseCase plateUseCase;

    private Plate plate;
    private Restaurant restaurant;
    private Long userId;
    private String userRole;

    @BeforeEach
    void setUp() {
        userId = 1L;
        userRole = "OWNER";

        plate = Plate.builder()
                .name("Shawarma")
                .price(new BigDecimal("25000.00"))
                .description("Tortilla, vegetales, carne, pollo, salsa de ajo")
                .urlImage("https://example.com/images/shawarma.jpg")
                .category("Shawarma")
                .restaurantId(1L)
                .active(true)
                .build();

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .ownerId(userId)
                .build();
    }

    @Test
    void shouldCreatePlateSuccessfully() {
        // Given
        Plate expectedPlate = plate.toBuilder().id(1L).build();
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);
        when(plateRepository.create(plate)).thenReturn(expectedPlate);

        // When
        Plate result = plateUseCase.createPlate(plate, userId, userRole);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Shawarma", result.getName());
        assertEquals(new BigDecimal("25000.00"), result.getPrice());
        assertEquals("Shawarma", result.getCategory());
        verify(restaurantRepository).findById(1L);
        verify(plateRepository).create(plate);
    }

    @Test
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Given
        when(restaurantRepository.findById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> plateUseCase.createPlate(plate, userId, userRole));

        assertEquals(DomainErrorCode.RESTAURANT_NOT_FOUND.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.RESTAURANT_NOT_FOUND.getMessage(), exception.getMessage());

        verify(restaurantRepository).findById(1L);
        verify(plateRepository, never()).create(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotRestaurantOwner() {
        // Given
        Long differentUserId = 2L;
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> plateUseCase.createPlate(plate, differentUserId, userRole));

        assertEquals(DomainErrorCode.RESTAURANT_NOT_OWNER.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.RESTAURANT_NOT_OWNER.getMessage(), exception.getMessage());

        verify(restaurantRepository).findById(1L);
        verify(plateRepository, never()).create(any());
    }
}
