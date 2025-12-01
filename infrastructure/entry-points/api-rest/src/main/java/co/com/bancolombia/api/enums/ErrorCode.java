package co.com.bancolombia.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    AUTHENTICATION_FAILED("AUTHENTICATION_FAILED", "Credenciales inválidas"),
    CONSTRAINT_VIOLATION("CONSTRAINT_VIOLATION", "Parámetros inválidos"),
    VALIDATION_ERROR("VALIDATION_ERROR", "Error de validación"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Error interno del servidor"),
    ACCESS_DENIED("ACCESS_DENIED", "No tienes permisos para acceder a este recurso");

    private final String code;
    private final String defaultMessage;
}