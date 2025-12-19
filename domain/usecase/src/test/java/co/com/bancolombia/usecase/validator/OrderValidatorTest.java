package co.com.bancolombia.usecase.validator;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.orderitem.OrderItem;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderValidatorTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PlateRepository plateRepository;

    private Order order;
    private OrderItem item1;
    private Plate plate1;

    @BeforeEach
    void setUp() {
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
                .status(OrderStatus.PENDING)
                .id(1L)
                .clientId(100L)
                .restaurantId(1L)
                .build();
    }

    @Test
    void shouldValidateOrderStructureSuccessfully() {
        // When & Then - no exception should be thrown
        assertDoesNotThrow(() -> OrderValidator.validateOrderStructure(order));
    }

    @Test
    void shouldThrowExceptionWhenOrderItemsIsNull() {
        // Given
        Order invalidOrder = Order.builder()
                .restaurantId(1L)
                .items(null)
                .build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderStructure(invalidOrder));
        assertEquals(DomainErrorCode.ORDER_ITEMS_REQUIRED.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenOrderItemsIsEmpty() {
        // Given
        Order emptyOrder = Order.builder()
                .restaurantId(1L)
                .items(List.of())
                .build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderStructure(emptyOrder));
        assertEquals(DomainErrorCode.ORDER_ITEMS_REQUIRED.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenItemQuantityIsZero() {
        // Given
        OrderItem invalidItem = OrderItem.builder()
                .plateId(1L)
                .quantity(0)
                .build();

        Order invalidOrder = Order.builder()
                .restaurantId(1L)
                .items(Arrays.asList(invalidItem))
                .build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderStructure(invalidOrder));
        assertEquals(DomainErrorCode.INVALID_ITEM_QUANTITY.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenItemQuantityIsNull() {
        // Given
        OrderItem invalidItem = OrderItem.builder()
                .plateId(1L)
                .quantity(null)
                .build();

        Order invalidOrder = Order.builder()
                .restaurantId(1L)
                .items(Arrays.asList(invalidItem))
                .build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderStructure(invalidOrder));
        assertEquals(DomainErrorCode.INVALID_ITEM_QUANTITY.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateClientHasNoActiveOrders() {
        // Given
        Long clientId = 100L;
        when(orderRepository.findByClientIdAndStatusIn(clientId, Arrays.asList(
                OrderStatus.PENDING, OrderStatus.IN_PREPARATION, OrderStatus.READY
        ))).thenReturn(Arrays.asList());

        // When & Then - no exception should be thrown
        assertDoesNotThrow(() -> OrderValidator.validateClientHasNoActiveOrders(clientId, orderRepository));
    }

    @Test
    void shouldThrowExceptionWhenClientHasActiveOrders() {
        // Given
        Long clientId = 100L;
        Order activeOrder = Order.builder().id(1L).status(OrderStatus.PENDING).build();

        when(orderRepository.findByClientIdAndStatusIn(clientId, Arrays.asList(
                OrderStatus.PENDING, OrderStatus.IN_PREPARATION, OrderStatus.READY
        ))).thenReturn(Arrays.asList(activeOrder));

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateClientHasNoActiveOrders(clientId, orderRepository));
        assertEquals(DomainErrorCode.CUSTOMER_HAS_ACTIVE_ORDER.getCode(), exception.getCode());
    }

    @Test
    void shouldValidatePlatesExistAndSameRestaurant() {
        // Given
        when(plateRepository.findById(1L)).thenReturn(plate1);

        // When & Then - no exception should be thrown
        assertDoesNotThrow(() -> OrderValidator.validatePlatesExistAndSameRestaurant(order, plateRepository));
    }

    @Test
    void shouldThrowExceptionWhenPlateNotFound() {
        // Given
        when(plateRepository.findById(1L)).thenReturn(null);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validatePlatesExistAndSameRestaurant(order, plateRepository));
        assertEquals(DomainErrorCode.PLATE_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenPlatesFromDifferentRestaurants() {
        // Given - order with 2 items from different restaurants
        OrderItem item2 = OrderItem.builder().plateId(2L).quantity(1).build();
        Order multiPlateOrder = Order.builder()
                .restaurantId(1L)
                .items(Arrays.asList(item1, item2))
                .build();

        Plate plate2 = Plate.builder()
                .id(2L)
                .restaurantId(2L)
                .build();

        when(plateRepository.findById(1L)).thenReturn(plate1);
        when(plateRepository.findById(2L)).thenReturn(plate2);

        // When & Then - should verify that different restaurant IDs throw exception
        assertThrows(BusinessException.class,
                () -> OrderValidator.validatePlatesExistAndSameRestaurant(multiPlateOrder, plateRepository));
    }

    @Test
    void shouldValidateOrderExists() {
        // When & Then - no exception for valid order
        assertDoesNotThrow(() -> OrderValidator.validateOrderExists(order));
    }

    @Test
    void shouldThrowExceptionWhenOrderIsNull() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderExists(null));
        assertEquals(DomainErrorCode.ORDER_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateOrderCanBeAssigned() {
        // When & Then
        assertDoesNotThrow(() -> OrderValidator.validateOrderCanBeAssigned(order));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotPending() {
        // Given
        Order readyOrder = order.toBuilder().status(OrderStatus.READY).build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderCanBeAssigned(readyOrder));
        assertEquals(DomainErrorCode.INVALID_ORDER_STATUS_TRANSITION.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenOrderAlreadyAssigned() {
        // Given
        Order assignedOrder = order.toBuilder().employeeId(50L).build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderCanBeAssigned(assignedOrder));
        assertEquals(DomainErrorCode.ORDER_ALREADY_ASSIGNED.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateOrderBelongsToRestaurant() {
        // When & Then
        assertDoesNotThrow(() -> OrderValidator.validateOrderBelongsToRestaurant(order, 1L));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotBelongsToRestaurant() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderBelongsToRestaurant(order, 999L));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateOrderCanBeMarkedAsReady() {
        // Given
        Order inPrepOrder = order.toBuilder().status(OrderStatus.IN_PREPARATION).build();

        // When & Then
        assertDoesNotThrow(() -> OrderValidator.validateOrderCanBeMarkedAsReady(inPrepOrder));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotInPreparation() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderCanBeMarkedAsReady(order));
        assertEquals(DomainErrorCode.INVALID_ORDER_STATUS_TRANSITION.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateOrderCanBeDelivered() {
        // Given
        Order readyOrder = order.toBuilder().status(OrderStatus.READY).build();

        // When & Then
        assertDoesNotThrow(() -> OrderValidator.validateOrderCanBeDelivered(readyOrder));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotReady() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderCanBeDelivered(order));
        assertEquals(DomainErrorCode.INVALID_ORDER_STATUS_TRANSITION.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateSecurityPinCorrect() {
        // Given
        Order orderWithPin = order.toBuilder().securityPin("1234").build();

        // When & Then
        assertDoesNotThrow(() -> OrderValidator.validateSecurityPin(orderWithPin, "1234"));
    }

    @Test
    void shouldThrowExceptionWhenSecurityPinIsNull() {
        // Given
        Order orderWithoutPin = order.toBuilder().securityPin(null).build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateSecurityPin(orderWithoutPin, "1234"));
        assertEquals(DomainErrorCode.INVALID_SECURITY_PIN.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenSecurityPinDoesNotMatch() {
        // Given
        Order orderWithPin = order.toBuilder().securityPin("1234").build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateSecurityPin(orderWithPin, "5678"));
        assertEquals(DomainErrorCode.INVALID_SECURITY_PIN.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateOrderBelongsToClient() {
        // When & Then
        assertDoesNotThrow(() -> OrderValidator.validateOrderBelongsToClient(order, 100L));
    }

    @Test
    void shouldThrowExceptionWhenOrderNotBelongsToClient() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderBelongsToClient(order, 999L));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateOrderCanBeCancelled() {
        // When & Then
        assertDoesNotThrow(() -> OrderValidator.validateOrderCanBeCancelled(order));
    }

    @Test
    void shouldThrowExceptionWhenOrderCannotBeCancelled() {
        // Given
        Order inPrepOrder = order.toBuilder().status(OrderStatus.IN_PREPARATION).build();

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> OrderValidator.validateOrderCanBeCancelled(inPrepOrder));
        assertEquals(DomainErrorCode.ORDER_CANNOT_BE_CANCELLED.getCode(), exception.getCode());
    }
}