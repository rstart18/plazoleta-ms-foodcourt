package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.OrderStatus;
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
class MarkOrderAsReadyUseCaseTest {

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
    void markOrderAsReady_ShouldUpdateOrderStatusAndSendNotification() {
        // Given
        Long orderId = 1L;
        Long employeeId = 2L;
        String userRole = "EMPLOYEE";
        String authToken = "Bearer token";
        Long restaurantId = 3L;
        String clientPhone = "+573001234567";

        Order existingOrder = Order.builder()
                .id(orderId)
                .clientId(100L)
                .clientEmail("client@test.com")
                .clientPhone(clientPhone)
                .restaurantId(restaurantId)
                .status(OrderStatus.IN_PREPARATION)
                .employeeEmail("employee@test.com")
                .build();

        Order updatedOrder = existingOrder.toBuilder()
                .status(OrderStatus.READY)
                .securityPin("1234")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(existingOrder);
        when(userGateway.getEmployeeRestaurantId(employeeId, authToken)).thenReturn(restaurantId);
        when(orderRepository.update(any(Order.class))).thenReturn(updatedOrder);

        // When
        Order result = orderUseCase.markOrderAsReady(orderId, employeeId, userRole, authToken);

        // Then
        assertEquals(OrderStatus.READY, result.getStatus());
        assertNotNull(result.getSecurityPin());
        
        verify(orderRepository).findById(orderId);
        verify(userGateway).getEmployeeRestaurantId(employeeId, authToken);
        verify(orderRepository).update(any(Order.class));
        verify(traceabilityGateway).sendOrderStatusChange(
                eq(orderId), eq(100L), eq("client@test.com"), 
                eq(OrderStatus.IN_PREPARATION), eq(OrderStatus.READY), 
                eq(employeeId), eq("employee@test.com")
        );
        verify(notificationGateway).sendOrderReadySms(eq(clientPhone), eq(orderId), anyString());
    }

    @Test
    void markOrderAsReady_ShouldGenerateSecurityPin() {
        // Given
        Long orderId = 1L;
        Long employeeId = 2L;
        String userRole = "EMPLOYEE";
        String authToken = "Bearer token";

        Order existingOrder = Order.builder()
                .id(orderId)
                .clientPhone("+573001234567")
                .restaurantId(3L)
                .status(OrderStatus.IN_PREPARATION)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(existingOrder);
        when(userGateway.getEmployeeRestaurantId(employeeId, authToken)).thenReturn(3L);
        when(orderRepository.update(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order result = orderUseCase.markOrderAsReady(orderId, employeeId, userRole, authToken);

        // Then
        assertNotNull(result.getSecurityPin());
        assertEquals(4, result.getSecurityPin().length());
        assertTrue(result.getSecurityPin().matches("\\d{4}"));
    }

    @Test
    void markOrderAsReady_ShouldCallNotificationWithCorrectParameters() {
        // Given
        Long orderId = 1L;
        Long employeeId = 2L;
        String clientPhone = "+573001234567";

        Order existingOrder = Order.builder()
                .id(orderId)
                .clientPhone(clientPhone)
                .restaurantId(3L)
                .status(OrderStatus.IN_PREPARATION)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(existingOrder);
        when(userGateway.getEmployeeRestaurantId(employeeId, "token")).thenReturn(3L);
        when(orderRepository.update(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        orderUseCase.markOrderAsReady(orderId, employeeId, "EMPLOYEE", "token");

        // Then
        verify(notificationGateway).sendOrderReadySms(eq(clientPhone), eq(orderId), anyString());
    }
}