package co.com.bancolombia.usecase.validator;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.order.gateway.OrderRepository;
import co.com.bancolombia.model.orderitem.OrderItem;
import co.com.bancolombia.model.plate.Plate;
import co.com.bancolombia.model.plate.gateways.PlateRepository;

import java.util.Arrays;
import java.util.List;

public class OrderValidator {

    public static void validateOrderStructure(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new BusinessException(
                    DomainErrorCode.ORDER_ITEMS_REQUIRED.getCode(),
                    DomainErrorCode.ORDER_ITEMS_REQUIRED.getMessage()
            );
        }

        validateItemsQuantity(order.getItems());
    }

    public static void validateClientHasNoActiveOrders(Long clientId, OrderRepository orderRepository) {
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.PENDING,
                OrderStatus.IN_PREPARATION,
                OrderStatus.READY
        );

        List<Order> activeOrders = orderRepository.findByClientIdAndStatusIn(clientId, activeStatuses);

        if (!activeOrders.isEmpty()) {
            throw new BusinessException(
                    DomainErrorCode.CUSTOMER_HAS_ACTIVE_ORDER.getCode(),
                    DomainErrorCode.CUSTOMER_HAS_ACTIVE_ORDER.getMessage()
            );
        }
    }

    public static void validatePlatesExistAndSameRestaurant(Order order, PlateRepository plateRepository) {
        Long restaurantId = null;
        for (OrderItem item : order.getItems()) {
            Plate plate = plateRepository.findById(item.getPlateId());
            if (plate == null) {
                throw new BusinessException(
                        DomainErrorCode.PLATE_NOT_FOUND.getCode(),
                        DomainErrorCode.PLATE_NOT_FOUND.getMessage()
                );
            }

            if (restaurantId == null) {
                restaurantId = plate.getRestaurantId();
            } else if (!restaurantId.equals(plate.getRestaurantId())) {
                throw new BusinessException(
                        DomainErrorCode.ORDER_PLATES_DIFFERENT_RESTAURANTS.getCode(),
                        DomainErrorCode.ORDER_PLATES_DIFFERENT_RESTAURANTS.getMessage()
                );
            }
        }
    }

    public static void validateOrderExists(Order order) {
        if (order == null) {
            throw new BusinessException(
                    DomainErrorCode.ORDER_NOT_FOUND.getCode(),
                    DomainErrorCode.ORDER_NOT_FOUND.getMessage()
            );
        }
    }

    public static void validateOrderCanBeAssigned(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException(
                    DomainErrorCode.INVALID_ORDER_STATUS_TRANSITION.getCode(),
                    DomainErrorCode.INVALID_ORDER_STATUS_TRANSITION.getMessage()
            );
        }

        if (order.getEmployeeId() != null) {
            throw new BusinessException(
                    DomainErrorCode.ORDER_ALREADY_ASSIGNED.getCode(),
                    DomainErrorCode.ORDER_ALREADY_ASSIGNED.getMessage()
            );
        }
    }

    public static void validateOrderBelongsToRestaurant(Order order, Long restaurantId) {
        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new BusinessException(
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(),
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getMessage()
            );
        }
    }

    private static void validateItemsQuantity(List<OrderItem> items) {
        for (OrderItem item : items) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new BusinessException(
                        DomainErrorCode.INVALID_ITEM_QUANTITY.getCode(),
                        DomainErrorCode.INVALID_ITEM_QUANTITY.getMessage()
                );
            }
        }
    }
}
