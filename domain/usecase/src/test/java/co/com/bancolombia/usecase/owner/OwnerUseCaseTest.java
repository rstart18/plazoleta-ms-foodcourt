package co.com.bancolombia.usecase.owner;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerUseCaseTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private OwnerUseCase ownerUseCase;

    private Long restaurantId;
    private Long ownerId;
    private Long differentOwnerId;
    private String userRole;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurantId = 1L;
        ownerId = 100L;
        differentOwnerId = 200L;
        userRole = "OWNER";

        restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Test Restaurant")
                .ownerId(ownerId)
                .nit("123456789")
                .build();
    }

    @Test
    void shouldValidateOwnerRestaurantSuccessfully() {
        // Given
        when(restaurantRepository.findById(restaurantId)).thenReturn(restaurant);

        // When
        boolean result = ownerUseCase.validateOwnerRestaurant(restaurantId, ownerId, userRole);

        // Then
        assertTrue(result);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void shouldReturnFalseWhenOwnerIdDoesNotMatch() {
        // Given
        when(restaurantRepository.findById(restaurantId)).thenReturn(restaurant);

        // When
        boolean result = ownerUseCase.validateOwnerRestaurant(restaurantId, differentOwnerId, userRole);

        // Then
        assertFalse(result);
        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenRestaurantNotFound() {
        // Given
        when(restaurantRepository.findById(restaurantId)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> ownerUseCase.validateOwnerRestaurant(restaurantId, ownerId, userRole));

        assertEquals(DomainErrorCode.RESTAURANT_NOT_FOUND.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.RESTAURANT_NOT_FOUND.getMessage(), exception.getMessage());

        verify(restaurantRepository).findById(restaurantId);
    }

    @Test
    void shouldThrowExceptionWhenUserRoleIsNotOwner() {
        // Given & When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> ownerUseCase.validateOwnerRestaurant(restaurantId, ownerId, "EMPLOYEE"));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());

        verify(restaurantRepository, never()).findById(any());
    }

    @Test
    void shouldThrowExceptionWhenUserRoleIsClient() {
        // Given & When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> ownerUseCase.validateOwnerRestaurant(restaurantId, ownerId, "CLIENT"));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());

        verify(restaurantRepository, never()).findById(any());
    }

    @Test
    void shouldThrowExceptionWhenUserRoleIsAdmin() {
        // Given & When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> ownerUseCase.validateOwnerRestaurant(restaurantId, ownerId, "ADMIN"));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());

        verify(restaurantRepository, never()).findById(any());
    }

    @Test
    void shouldValidateCorrectlyWithDifferentRestaurants() {
        // Given
        Restaurant restaurant2 = Restaurant.builder()
                .id(2L)
                .name("Another Restaurant")
                .ownerId(differentOwnerId)
                .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(restaurant);
        when(restaurantRepository.findById(2L)).thenReturn(restaurant2);

        // When & Then - restaurant 1
        boolean result1 = ownerUseCase.validateOwnerRestaurant(restaurantId, ownerId, userRole);
        assertTrue(result1);

        // When & Then - restaurant 2 with different owner
        boolean result2 = ownerUseCase.validateOwnerRestaurant(2L, differentOwnerId, userRole);
        assertTrue(result2);

        // When & Then - restaurant 1 with wrong owner
        boolean result3 = ownerUseCase.validateOwnerRestaurant(restaurantId, differentOwnerId, userRole);
        assertFalse(result3);
    }

    @Test
    void shouldValidateWithDifferentOwnerIds() {
        // Given
        Long differentOwnerId2 = 300L;
        when(restaurantRepository.findById(restaurantId)).thenReturn(restaurant);

        // When
        boolean resultCorrectOwner = ownerUseCase.validateOwnerRestaurant(restaurantId, ownerId, userRole);
        boolean resultWrongOwner1 = ownerUseCase.validateOwnerRestaurant(restaurantId, differentOwnerId, userRole);
        boolean resultWrongOwner2 = ownerUseCase.validateOwnerRestaurant(restaurantId, differentOwnerId2, userRole);

        // Then
        assertTrue(resultCorrectOwner);
        assertFalse(resultWrongOwner1);
        assertFalse(resultWrongOwner2);
    }
}