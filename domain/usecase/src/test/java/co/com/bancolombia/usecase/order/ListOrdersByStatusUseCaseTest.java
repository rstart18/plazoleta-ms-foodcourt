package co.com.bancolombia.usecase.order;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
import co.com.bancolombia.model.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListOrdersByStatusUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PlateRepository plateRepository;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private OrderUseCase orderUseCase;

    private Long employeeId;
    private String userRole;
    private String authToken;
    private OrderStatus status;
    private Long restaurantId;

    @BeforeEach
    void setUp() {
        employeeId = 1L;
        userRole = "EMPLOYEE";
        authToken = "Bearer token";
        status = OrderStatus.PENDING;
        restaurantId = 10L;
    }

    @Test
    void shouldListOrdersByStatusSuccessfully() {
        // Given
        List<Order> orders = Arrays.asList(
                Order.builder().id(1L).restaurantId(restaurantId).status(status).build(),
                Order.builder().id(2L).restaurantId(restaurantId).status(status).build()
        );
        PagedResult<Order> expectedResult = PagedResult.<Order>builder()
                .content(orders)
                .pageNumber(0)
                .pageSize(10)
                .totalElements(2L)
                .totalPages(1)
                .build();

        when(userGateway.getEmployeeRestaurantId(employeeId, authToken)).thenReturn(restaurantId);
        when(orderRepository.findByStatusAndRestaurantId(status, restaurantId, 0, 10)).thenReturn(expectedResult);

        // When
        PagedResult<Order> result = orderUseCase.listOrdersByStatus(status, 0, 10, employeeId, userRole, authToken);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(0, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertEquals(2L, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        
        verify(userGateway).getEmployeeRestaurantId(employeeId, authToken);
        verify(orderRepository).findByStatusAndRestaurantId(status, restaurantId, 0, 10);
    }

    @Test
    void shouldThrowExceptionWhenUserRoleIsNotEmployee() {
        // Given
        String invalidRole = "CLIENT";

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> orderUseCase.listOrdersByStatus(status, 0, 10, employeeId, invalidRole, authToken));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getMessage(), exception.getMessage());

        verify(userGateway, never()).getEmployeeRestaurantId(any(), any());
        verify(orderRepository, never()).findByStatusAndRestaurantId(any(), any(), anyInt(), anyInt());
    }

    @Test
    void shouldReturnEmptyResultWhenEmployeeHasNoRestaurant() {
        // Given
        when(userGateway.getEmployeeRestaurantId(employeeId, authToken)).thenReturn(null);
        when(orderRepository.findByStatusAndRestaurantId(status, null, 0, 10))
                .thenReturn(PagedResult.<Order>builder()
                        .content(Arrays.asList())
                        .pageNumber(0)
                        .pageSize(10)
                        .totalElements(0L)
                        .totalPages(0)
                        .build());

        // When
        PagedResult<Order> result = orderUseCase.listOrdersByStatus(status, 0, 10, employeeId, userRole, authToken);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0L, result.getTotalElements());
        
        verify(userGateway).getEmployeeRestaurantId(employeeId, authToken);
        verify(orderRepository).findByStatusAndRestaurantId(status, null, 0, 10);
    }
}