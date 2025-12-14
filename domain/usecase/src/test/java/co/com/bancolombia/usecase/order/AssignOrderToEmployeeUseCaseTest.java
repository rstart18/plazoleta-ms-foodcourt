package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignOrderToEmployeeUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private Order pendingOrder;
    private Long employeeId;
    private String userRole;
    private String authToken;

    @BeforeEach
    void setUp() {
        employeeId = 1L;
        userRole = "EMPLOYEE";
        authToken = "Bearer token";

        pendingOrder = Order.builder()
                .id(1L)
                .customerId(100L)
                .restaurantId(1L)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldAssignOrderToEmployeeSuccessfully() {
        // Given
        Order expectedOrder = pendingOrder.toBuilder()
                .employeeId(employeeId)
                .status(OrderStatus.IN_PREPARATION)
                .updatedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(pendingOrder);
        when(userGateway.getEmployeeRestaurantId(employeeId, authToken)).thenReturn(1L);
        when(orderRepository.update(any(Order.class))).thenReturn(expectedOrder);

        // When
        Order result = orderUseCase.assignOrderToEmployee(1L, employeeId, userRole, authToken);

        // Then
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(OrderStatus.IN_PREPARATION, result.getStatus());
        assertNotNull(result.getUpdatedAt());

        verify(orderRepository).findById(1L);
        verify(userGateway).getEmployeeRestaurantId(employeeId, authToken);
        verify(orderRepository).update(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.assignOrderToEmployee(1L, employeeId, userRole, authToken));

        assertEquals(DomainErrorCode.ORDER_NOT_FOUND.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(userGateway, never()).getEmployeeRestaurantId(any(), any());
        verify(orderRepository, never()).update(any());
    }

    @Test
    void shouldThrowExceptionWhenOrderAlreadyAssigned() {
        // Given
        Order assignedOrder = pendingOrder.toBuilder()
                .employeeId(2L)
                .build();
        when(orderRepository.findById(1L)).thenReturn(assignedOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.assignOrderToEmployee(1L, employeeId, userRole, authToken));

        assertEquals(DomainErrorCode.ORDER_ALREADY_ASSIGNED.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(userGateway, never()).getEmployeeRestaurantId(any(), any());
        verify(orderRepository, never()).update(any());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotPending() {
        // Given
        Order inPreparationOrder = pendingOrder.toBuilder()
                .status(OrderStatus.IN_PREPARATION)
                .build();
        when(orderRepository.findById(1L)).thenReturn(inPreparationOrder);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.assignOrderToEmployee(1L, employeeId, userRole, authToken));

        assertEquals(DomainErrorCode.INVALID_ORDER_STATUS_TRANSITION.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(userGateway, never()).getEmployeeRestaurantId(any(), any());
        verify(orderRepository, never()).update(any());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFromSameRestaurant() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(pendingOrder);
        when(userGateway.getEmployeeRestaurantId(employeeId, authToken)).thenReturn(2L);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.assignOrderToEmployee(1L, employeeId, userRole, authToken));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        verify(orderRepository).findById(1L);
        verify(userGateway).getEmployeeRestaurantId(employeeId, authToken);
        verify(orderRepository, never()).update(any());
    }
}