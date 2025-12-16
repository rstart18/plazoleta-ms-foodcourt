package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import co.com.bancolombia.model.user.gateways.UserGateway;
import co.com.bancolombia.model.notification.gateways.NotificationGateway;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliverOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PlateRepository plateRepository;
    @Mock
    private UserGateway userGateway;
    @Mock
    private TraceabilityGateway traceabilityGateway;
    @Mock
    private NotificationGateway notificationGateway;

    private OrderUseCase orderUseCase;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(orderRepository, plateRepository, userGateway, traceabilityGateway, notificationGateway);
    }

    @Test
    void deliverOrder_ShouldUpdateOrderStatusToDelivered() {
        // Given
        Long orderId = 1L;
        String securityPin = "1234";
        Long employeeId = 2L;
        String userRole = "EMPLOYEE";
        String authToken = "Bearer token";
        Long restaurantId = 3L;

        Order readyOrder = Order.builder()
                .id(orderId)
                .clientId(100L)
                .clientEmail("client@test.com")
                .restaurantId(restaurantId)
                .status(OrderStatus.READY)
                .securityPin(securityPin)
                .employeeEmail("employee@test.com")
                .build();

        Order deliveredOrder = readyOrder.toBuilder()
                .status(OrderStatus.DELIVERED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(readyOrder);
        when(userGateway.getEmployeeRestaurantId(employeeId, authToken)).thenReturn(restaurantId);
        when(orderRepository.update(any(Order.class))).thenReturn(deliveredOrder);

        // When
        Order result = orderUseCase.deliverOrder(orderId, securityPin, employeeId, userRole, authToken);

        // Then
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        
        verify(orderRepository).findById(orderId);
        verify(userGateway).getEmployeeRestaurantId(employeeId, authToken);
        verify(orderRepository).update(any(Order.class));
        verify(traceabilityGateway).sendOrderStatusChange(
                eq(orderId), eq(100L), eq("client@test.com"), 
                eq(OrderStatus.READY), eq(OrderStatus.DELIVERED), 
                eq(employeeId), eq("employee@test.com")
        );
    }

    @Test
    void deliverOrder_ShouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.deliverOrder(1L, "1234", 2L, "EMPLOYEE", "token"));

        assertEquals(DomainErrorCode.ORDER_NOT_FOUND.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void deliverOrder_ShouldThrowExceptionWhenOrderNotReady() {
        // Given
        Order inPreparationOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.IN_PREPARATION)
                .restaurantId(3L)
                .build();

        when(orderRepository.findById(1L)).thenReturn(inPreparationOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.deliverOrder(1L, "1234", 2L, "EMPLOYEE", "token"));

        assertEquals(DomainErrorCode.INVALID_ORDER_STATUS_TRANSITION.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void deliverOrder_ShouldThrowExceptionWhenInvalidSecurityPin() {
        // Given
        Order readyOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .securityPin("1234")
                .restaurantId(3L)
                .build();

        when(orderRepository.findById(1L)).thenReturn(readyOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.deliverOrder(1L, "5678", 2L, "EMPLOYEE", "token"));

        assertEquals(DomainErrorCode.INVALID_SECURITY_PIN.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void deliverOrder_ShouldThrowExceptionWhenEmployeeNotFromSameRestaurant() {
        // Given
        Order readyOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.READY)
                .securityPin("1234")
                .restaurantId(3L)
                .build();

        when(orderRepository.findById(1L)).thenReturn(readyOrder);
        when(userGateway.getEmployeeRestaurantId(2L, "token")).thenReturn(5L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.deliverOrder(1L, "1234", 2L, "EMPLOYEE", "token"));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(userGateway).getEmployeeRestaurantId(2L, "token");
        verify(orderRepository, never()).update(any());
    }

    @Test
    void deliverOrder_ShouldValidateSecurityPinCorrectly() {
        // Given
        Long orderId = 1L;
        String correctPin = "1234";
        Long employeeId = 2L;
        Long restaurantId = 3L;

        Order readyOrder = Order.builder()
                .id(orderId)
                .status(OrderStatus.READY)
                .securityPin(correctPin)
                .restaurantId(restaurantId)
                .clientId(100L)
                .clientEmail("client@test.com")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(readyOrder);
        when(userGateway.getEmployeeRestaurantId(employeeId, "token")).thenReturn(restaurantId);
        when(orderRepository.update(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order result = orderUseCase.deliverOrder(orderId, correctPin, employeeId, "EMPLOYEE", "token");

        // Then
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderRepository).update(any(Order.class));
    }
}