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
class CancelOrderUseCaseTest {

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
    void cancelOrder_ShouldUpdateOrderStatusToCancelled() {
        // Given
        Long orderId = 1L;
        Long clientId = 100L;
        String userRole = "CLIENT";

        Order pendingOrder = Order.builder()
                .id(orderId)
                .clientId(clientId)
                .clientEmail("client@test.com")
                .status(OrderStatus.PENDING)
                .build();

        Order cancelledOrder = pendingOrder.toBuilder()
                .status(OrderStatus.CANCELLED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(pendingOrder);
        when(orderRepository.update(any(Order.class))).thenReturn(cancelledOrder);

        // When
        Order result = orderUseCase.cancelOrder(orderId, clientId, userRole);

        // Then
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        
        verify(orderRepository).findById(orderId);
        verify(orderRepository).update(any(Order.class));
        verify(traceabilityGateway).sendOrderStatusChange(
                eq(orderId), eq(clientId), eq("client@test.com"), 
                eq(OrderStatus.PENDING), eq(OrderStatus.CANCELLED), 
                isNull(), isNull()
        );
    }

    @Test
    void cancelOrder_ShouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.cancelOrder(1L, 100L, "CLIENT"));

        assertEquals(DomainErrorCode.ORDER_NOT_FOUND.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void cancelOrder_ShouldThrowExceptionWhenOrderNotBelongsToClient() {
        // Given
        Order order = Order.builder()
                .id(1L)
                .clientId(200L) // Different client
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(order);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.cancelOrder(1L, 100L, "CLIENT"));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void cancelOrder_ShouldThrowExceptionWhenOrderNotPending() {
        // Given
        Order inPreparationOrder = Order.builder()
                .id(1L)
                .clientId(100L)
                .status(OrderStatus.IN_PREPARATION)
                .build();

        when(orderRepository.findById(1L)).thenReturn(inPreparationOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.cancelOrder(1L, 100L, "CLIENT"));

        assertEquals(DomainErrorCode.ORDER_CANNOT_BE_CANCELLED.getCode(), exception.getCode());
        assertEquals("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse", exception.getMessage());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void cancelOrder_ShouldThrowExceptionWhenOrderReady() {
        // Given
        Order readyOrder = Order.builder()
                .id(1L)
                .clientId(100L)
                .status(OrderStatus.READY)
                .build();

        when(orderRepository.findById(1L)).thenReturn(readyOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.cancelOrder(1L, 100L, "CLIENT"));

        assertEquals(DomainErrorCode.ORDER_CANNOT_BE_CANCELLED.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void cancelOrder_ShouldThrowExceptionWhenOrderDelivered() {
        // Given
        Order deliveredOrder = Order.builder()
                .id(1L)
                .clientId(100L)
                .status(OrderStatus.DELIVERED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(deliveredOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.cancelOrder(1L, 100L, "CLIENT"));

        assertEquals(DomainErrorCode.ORDER_CANNOT_BE_CANCELLED.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).update(any());
    }

    @Test
    void cancelOrder_ShouldValidateClientRole() {
        // Given
        Order pendingOrder = Order.builder()
                .id(1L)
                .clientId(100L)
                .status(OrderStatus.PENDING)
                .build();

        when(orderRepository.findById(1L)).thenReturn(pendingOrder);
        when(orderRepository.update(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Order result = orderUseCase.cancelOrder(1L, 100L, "CLIENT");

        // Then
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        verify(orderRepository).update(any(Order.class));
    }
}