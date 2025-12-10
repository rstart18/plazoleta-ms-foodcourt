package co.com.bancolombia.usecase.restaurant;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.restaurant.Restaurant;
import co.com.bancolombia.model.restaurant.gateways.RestaurantRepository;
import co.com.bancolombia.model.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantUseCaseTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private RestaurantUseCase createRestaurantUseCase;

    private Restaurant restaurant;
    private String authToken;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .name("Restaurante El Desayunadero")
                .nit("123437789")
                .address("Calle 123 #45-67, Bogotá")
                .phone("+573008334567")
                .urlLogo("https://example.com/logo.png")
                .ownerId(1L)
                .build();

        authToken = "Bearer eyJhbGciOiJIUzI1NiJ9...";
    }

    @Test
    void shouldCreateRestaurantSuccessfully() {
        // Given
        Restaurant expectedRestaurant = restaurant.toBuilder().id(1L).build();
        when(restaurantRepository.existsByNit("123437789")).thenReturn(false);
        when(userGateway.hasOwnerRole(1L, authToken)).thenReturn(true);
        when(restaurantRepository.create(restaurant)).thenReturn(expectedRestaurant);

        // When
        Restaurant result = createRestaurantUseCase.createRestaurant(restaurant, authToken);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Restaurante El Desayunadero", result.getName());
        assertEquals("123437789", result.getNit());
        verify(restaurantRepository).existsByNit("123437789");
        verify(userGateway).hasOwnerRole(1L, authToken);
        verify(restaurantRepository).create(restaurant);
    }

    @Test
    void shouldThrowExceptionWhenNitAlreadyExists() {
        // Given
        when(restaurantRepository.existsByNit("123437789")).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant, authToken));

        assertEquals(DomainErrorCode.RESTAURANT_NIT_ALREADY_EXISTS.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.RESTAURANT_NIT_ALREADY_EXISTS.getMessage(), exception.getMessage());

        verify(restaurantRepository).existsByNit("123437789");
        verify(userGateway, never()).hasOwnerRole(any(), any());
        verify(restaurantRepository, never()).create(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        // Given
        when(restaurantRepository.existsByNit("123437789")).thenReturn(false);
        when(userGateway.hasOwnerRole(1L, authToken)).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> createRestaurantUseCase.createRestaurant(restaurant, authToken));

        assertEquals(DomainErrorCode.USER_NOT_OWNER.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.USER_NOT_OWNER.getMessage(), exception.getMessage());

        verify(restaurantRepository).existsByNit("123437789");
        verify(userGateway).hasOwnerRole(1L, authToken);
        verify(restaurantRepository, never()).create(any());
    }
}
