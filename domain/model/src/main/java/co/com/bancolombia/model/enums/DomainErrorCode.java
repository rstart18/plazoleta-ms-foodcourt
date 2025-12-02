package co.com.bancolombia.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomainErrorCode {
    RESTAURANT_NOT_FOUND("RESTAURANT_NOT_FOUND", "Restaurante no encontrado"),
    RESTAURANT_NIT_ALREADY_EXISTS("RESTAURANT_NIT_ALREADY_EXISTS", "Ya existe un restaurante con este NIT"),
    RESTAURANT_NAME_INVALID("RESTAURANT_NAME_INVALID", "El nombre del restaurante no puede ser solo números"),
    INVALID_PHONE_FORMAT("INVALID_PHONE_FORMAT", "Formato de teléfono inválido"),
    INVALID_NIT_FORMAT("INVALID_NIT_FORMAT", "El NIT debe contener únicamente números"),
    USER_NOT_OWNER("USER_NOT_OWNER", "El usuario no tiene rol de propietario");

    private final String code;
    private final String message;
}