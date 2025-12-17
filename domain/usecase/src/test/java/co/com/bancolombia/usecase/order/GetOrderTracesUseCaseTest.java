package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.traceability.OrderTrace;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import co.com.bancolombia.model.user.gateways.UserGateway;
import co.com.bancolombia.model.notification.gateways.NotificationGateway;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrderTracesUseCaseTest {

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
    void shouldGetOrderTracesSuccessfully() {
        // Given
        Long orderId = 6L;
        LocalDateTime timestamp1 = LocalDateTime.of(2025, 12, 15, 1, 11, 34);
        LocalDateTime timestamp2 = LocalDateTime.of(2025, 12, 15, 1, 14, 33);

        List<OrderTrace> expectedTraces = Arrays.asList(
            OrderTrace.builder()
                .id(3L)
                .orderId(orderId)
                .clientId(10L)
                .clientEmail("juan.sanchez@example.com")
                .previousStatus(null)
                .newStatus(OrderStatus.PENDING)
                .employeeId(null)
                .employeeEmail(null)
                .timestamp(timestamp1)
                .build(),
            OrderTrace.builder()
                .id(4L)
                .orderId(orderId)
                .clientId(10L)
                .clientEmail("juan.sanchez@example.com")
                .previousStatus(OrderStatus.PENDING)
                .newStatus(OrderStatus.IN_PREPARATION)
                .employeeId(11L)
                .employeeEmail("ana.armani@restaurant.com")
                .timestamp(timestamp2)
                .build()
        );

        when(traceabilityGateway.getOrderTraces(orderId)).thenReturn(expectedTraces);

        // When
        List<OrderTrace> result = orderUseCase.getOrderTraces(orderId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        OrderTrace trace1 = result.get(0);
        assertEquals(3L, trace1.getId());
        assertEquals(orderId, trace1.getOrderId());
        assertEquals(10L, trace1.getClientId());
        assertEquals("juan.sanchez@example.com", trace1.getClientEmail());
        assertNull(trace1.getPreviousStatus());
        assertEquals(OrderStatus.PENDING, trace1.getNewStatus());
        assertNull(trace1.getEmployeeId());
        assertNull(trace1.getEmployeeEmail());
        assertEquals(timestamp1, trace1.getTimestamp());

        OrderTrace trace2 = result.get(1);
        assertEquals(4L, trace2.getId());
        assertEquals(orderId, trace2.getOrderId());
        assertEquals(OrderStatus.PENDING, trace2.getPreviousStatus());
        assertEquals(OrderStatus.IN_PREPARATION, trace2.getNewStatus());
        assertEquals(11L, trace2.getEmployeeId());
        assertEquals("ana.armani@restaurant.com", trace2.getEmployeeEmail());

        verify(traceabilityGateway).getOrderTraces(orderId);
    }

    @Test
    void shouldReturnEmptyListWhenNoTracesFound() {
        // Given
        Long orderId = 999L;
        List<OrderTrace> emptyTraces = List.of();

        when(traceabilityGateway.getOrderTraces(orderId)).thenReturn(emptyTraces);

        // When
        List<OrderTrace> result = orderUseCase.getOrderTraces(orderId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(traceabilityGateway).getOrderTraces(orderId);
    }

    @Test
    void shouldPropagateExceptionFromGateway() {
        // Given
        Long orderId = 1L;
        RuntimeException expectedException = new RuntimeException("Error getting order traces");

        when(traceabilityGateway.getOrderTraces(orderId)).thenThrow(expectedException);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderUseCase.getOrderTraces(orderId));

        assertEquals("Error getting order traces", exception.getMessage());
        verify(traceabilityGateway).getOrderTraces(orderId);
    }
}