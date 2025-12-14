package co.com.bancolombia.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainErrorCode {
    INVALID_TOKEN("INVALID_TOKEN", "Token JWT inválido"),
    RESTAURANT_NOT_FOUND("RESTAURANT_NOT_FOUND", "Restaurante no encontrado"),
    RESTAURANT_NIT_ALREADY_EXISTS("RESTAURANT_NIT_ALREADY_EXISTS", "Ya existe un restaurante con este NIT"),
    RESTAURANT_NAME_INVALID("RESTAURANT_NAME_INVALID", "El nombre del restaurante no puede ser solo números"),
    INVALID_PHONE_FORMAT("INVALID_PHONE_FORMAT", "Formato de teléfono inválido"),
    INVALID_NIT_FORMAT("INVALID_NIT_FORMAT", "El NIT debe contener únicamente números"),
    RESTAURANT_NOT_OWNER("RESTAURANT_NOT_OWNER", "El usuario no es propietario del restaurante"),
    USER_NOT_OWNER("USER_NOT_OWNER", "El usuario no tiene rol de propietario"),
    PLATE_NOT_FOUND("PLATE_NOT_FOUND", "Plato no encontrado"),
    CUSTOMER_HAS_ACTIVE_ORDER("CUSTOMER_HAS_ACTIVE_ORDER", "Customer already has an active order"),
    ORDER_ITEMS_REQUIRED("ORDER_ITEMS_REQUIRED", "Order must contain at least one item"),
    ORDER_PLATES_DIFFERENT_RESTAURANTS("ORDER_PLATES_DIFFERENT_RESTAURANTS", "All plates in an order must be from the same restaurant"),
    INVALID_ITEM_QUANTITY("INVALID_ITEM_QUANTITY", "Item quantity must be greater than zero"),
    INSUFFICIENT_PERMISSIONS("INSUFFICIENT_PERMISSIONS", "User does not have required permissions"),
    ORDER_NOT_FOUND("ORDER_NOT_FOUND", "Order not found"),
    ORDER_ALREADY_ASSIGNED("ORDER_ALREADY_ASSIGNED", "Order is already assigned to an employee"),
    INVALID_ORDER_STATUS_TRANSITION("INVALID_ORDER_STATUS_TRANSITION", "Invalid order status transition");

    private final String code;
    private final String message;
}