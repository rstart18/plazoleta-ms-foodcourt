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
class TogglePlateStatusUseCaseTest {

    @Mock
    private PlateRepository plateRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private PlateUseCase plateUseCase;

    private Plate activePlate;
    private Plate inactivePlate;
    private Restaurant restaurant;
    private Long plateId;
    private Long userId;
    private String userRole;

    @BeforeEach
    void setUp() {
        plateId = 1L;
        userId = 1L;
        userRole = "OWNER";

        activePlate = Plate.builder()
                .id(plateId)
                .name("Pizza Margherita")
                .price(new BigDecimal("25000.00"))
                .description("Pizza con tomate y mozzarella")
                .category("PIZZA")
                .restaurantId(1L)
                .active(true)
                .build();

        inactivePlate = Plate.builder()
                .id(plateId)
                .name("Pizza Margherita")
                .price(new BigDecimal("25000.00"))
                .description("Pizza con tomate y mozzarella")
                .category("PIZZA")
                .restaurantId(1L)
                .active(false)
                .build();

        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .ownerId(userId)
                .build();
    }

    @Test
    void shouldTogglePlateFromActiveToInactive() {
        // Given
        Plate expectedInactivePlate = activePlate.toBuilder()
                .active(false)
                .build();

        when(plateRepository.findById(plateId)).thenReturn(activePlate);
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);
        when(plateRepository.update(any(Plate.class))).thenReturn(expectedInactivePlate);

        // When
        Plate result = plateUseCase.togglePlateStatus(plateId, userId, userRole);

        // Then
        assertNotNull(result);
        assertFalse(result.getActive());
        assertEquals(activePlate.getName(), result.getName());
        assertEquals(activePlate.getId(), result.getId());

        verify(plateRepository).findById(plateId);
        verify(restaurantRepository).findById(1L);
        verify(plateRepository).update(any(Plate.class));
    }

    @Test
    void shouldTogglePlateFromInactiveToActive() {
        // Given
        Plate expectedActivePlate = inactivePlate.toBuilder()
                .active(true)
                .build();

        when(plateRepository.findById(plateId)).thenReturn(inactivePlate);
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);
        when(plateRepository.update(any(Plate.class))).thenReturn(expectedActivePlate);

        // When
        Plate result = plateUseCase.togglePlateStatus(plateId, userId, userRole);

        // Then
        assertNotNull(result);
        assertTrue(result.getActive());
        assertEquals(inactivePlate.getName(), result.getName());
        assertEquals(inactivePlate.getId(), result.getId());

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
                () -> plateUseCase.togglePlateStatus(plateId, userId, userRole));

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
        when(plateRepository.findById(plateId)).thenReturn(activePlate);
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> plateUseCase.togglePlateStatus(plateId, differentUserId, userRole));

        assertEquals(DomainErrorCode.RESTAURANT_NOT_OWNER.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.RESTAURANT_NOT_OWNER.getMessage(), exception.getMessage());

        verify(plateRepository).findById(plateId);
        verify(restaurantRepository).findById(1L);
        verify(plateRepository, never()).update(any());
    }

    @Test
    void shouldOnlyChangeActiveStatusAndKeepOtherFieldsUnchanged() {
        // Given
        when(plateRepository.findById(plateId)).thenReturn(activePlate);
        when(restaurantRepository.findById(1L)).thenReturn(restaurant);
        when(plateRepository.update(any(Plate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Plate result = plateUseCase.togglePlateStatus(plateId, userId, userRole);

        // Then
        assertFalse(result.getActive()); // Solo debe cambiar el estado

        // Todos los demás campos deben permanecer igual
        assertEquals(activePlate.getName(), result.getName());
        assertEquals(activePlate.getPrice(), result.getPrice());
        assertEquals(activePlate.getDescription(), result.getDescription());
        assertEquals(activePlate.getCategory(), result.getCategory());
        assertEquals(activePlate.getRestaurantId(), result.getRestaurantId());
        assertEquals(activePlate.getUrlImage(), result.getUrlImage());
    }
}
