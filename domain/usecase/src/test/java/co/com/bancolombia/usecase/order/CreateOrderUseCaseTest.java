package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.orderitem.OrderItem;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import co.com.bancolombia.model.user.gateways.UserGateway;
import co.com.bancolombia.model.notification.gateways.NotificationGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

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
    private Long clientId;
    private Order order;
    private OrderItem item1;
    private Plate plate1;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(orderRepository, plateRepository, userGateway, traceabilityGateway, notificationGateway);
        clientId = 100L;

        plate1 = Plate.builder()
                .id(1L)
                .name("Pizza")
                .price(new BigDecimal("25000.00"))
                .restaurantId(1L)
                .build();

        item1 = OrderItem.builder()
                .plateId(1L)
                .quantity(2)
                .build();

        order = Order.builder()
                .restaurantId(1L)
                .items(Arrays.asList(item1))
                .build();
    }

    @Test
    void shouldThrowExceptionWhenUserRoleIsNotClient() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.createOrder(order, clientId, "client@test.com", "+5730123456", "EMPLOYEE"));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        verify(orderRepository, never()).create(any());
    }

    @Test
    void shouldThrowExceptionWhenOrderHasNoItems() {
        // Given
        Order emptyOrder = Order.builder()
                .restaurantId(1L)
                .items(List.of())
                .build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.createOrder(emptyOrder, clientId, "client@test.com", "+5730123456", "CLIENT"));

        assertEquals(DomainErrorCode.ORDER_ITEMS_REQUIRED.getCode(), exception.getCode());
        verify(plateRepository, never()).findById(any());
        verify(orderRepository, never()).create(any());
    }

    @Test
    void shouldThrowExceptionWhenClientHasActiveOrder() {
        // Given
        Order activeOrder = Order.builder()
                .id(10L)
                .clientId(clientId)
                .status(OrderStatus.IN_PREPARATION)
                .build();

        when(orderRepository.findByClientIdAndStatusIn(eq(clientId), any())).thenReturn(Arrays.asList(activeOrder));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.createOrder(order, clientId, "client@test.com", "+5730123456", "CLIENT"));

        assertEquals(DomainErrorCode.CUSTOMER_HAS_ACTIVE_ORDER.getCode(), exception.getCode());
        verify(plateRepository, never()).findById(any());
        verify(orderRepository, never()).create(any());
    }

    @Test
    void shouldThrowExceptionWhenPlateNotFound() {
        // Given
        when(orderRepository.findByClientIdAndStatusIn(eq(clientId), any())).thenReturn(Arrays.asList());
        when(plateRepository.findById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.createOrder(order, clientId, "client@test.com", "+5730123456", "CLIENT"));

        assertEquals(DomainErrorCode.PLATE_NOT_FOUND.getCode(), exception.getCode());
        verify(orderRepository, never()).create(any());
    }

    @Test
    void shouldThrowExceptionWhenUserRoleIsAdmin() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.createOrder(order, clientId, "client@test.com", "+5730123456", "ADMIN"));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        verify(orderRepository, never()).create(any());
        verify(plateRepository, never()).findById(any());
    }

    @Test
    void shouldValidateRoleBeforeOtherValidations() {
        // Given - invalid order structure but wrong role should fail first
        Order emptyOrder = Order.builder().restaurantId(1L).items(List.of()).build();

        // When & Then - role validation should happen before structure validation
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.createOrder(emptyOrder, clientId, "client@test.com", "+5730123456", "OWNER"));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }
}