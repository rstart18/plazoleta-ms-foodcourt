package co.com.bancolombia.api.exception;

import co.com.bancolombia.api.dto.response.ErrorObjectResponse;
import co.com.bancolombia.api.dto.response.ResponseData;
import co.com.bancolombia.model.exception.BusinessException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOGGER_PREFIX = String.format("[%s]", GlobalExceptionHandler.class.getSimpleName());
    private static final Pattern ARRAY_INDEX_PATTERN = Pattern.compile("\\[\\d+\\]");
    private static final String INVALID_PARAMETER = "Parámetro inválido";

    private static final Map<Class<? extends Exception>, HttpStatus> HTTP_STATUS_BY_EXCEPTION = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(BusinessException.class, HttpStatus.BAD_REQUEST),
            new AbstractMap.SimpleEntry<>(AuthenticationException.class, HttpStatus.UNAUTHORIZED),
            new AbstractMap.SimpleEntry<>(AuthorizationDeniedException.class, HttpStatus.FORBIDDEN),
            new AbstractMap.SimpleEntry<>(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST),
            new AbstractMap.SimpleEntry<>(ConstraintViolationException.class, HttpStatus.BAD_REQUEST)
    );

    private static final Map<Class<? extends Exception>, ExceptionConfig> EXCEPTION_CONFIG = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(BusinessException.class, new ExceptionConfig(null, null)),
            new AbstractMap.SimpleEntry<>(AuthenticationException.class, new ExceptionConfig("AUTHENTICATION_FAILED", "Credenciales inválidas")),
            new AbstractMap.SimpleEntry<>(AuthorizationDeniedException.class, new ExceptionConfig("ACCESS_DENIED", "Acceso denegado")),
            new AbstractMap.SimpleEntry<>(MethodArgumentNotValidException.class, new ExceptionConfig("INVALID_PARAMETERS", null)),
            new AbstractMap.SimpleEntry<>(ConstraintViolationException.class, new ExceptionConfig("CONSTRAINT_VIOLATION", null))
    );

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseData<List<ErrorObjectResponse>>> handleValidationException(
            final MethodArgumentNotValidException ex
    ) {
        log.error("{} [handleValidationException] Validation error", LOGGER_PREFIX, ex);

        final List<ErrorObjectResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorObjectResponse(
                        ARRAY_INDEX_PATTERN.matcher(error.getField()).replaceAll(""),
                        Optional.ofNullable(error.getDefaultMessage()).orElse(INVALID_PARAMETER)
                ))
                .collect(Collectors.toMap(
                        ErrorObjectResponse::getField,
                        e -> e,
                        (existing, duplicate) -> existing
                ))
                .values()
                .stream()
                .toList();

        return buildResponse(errors, ex.getClass());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseData<List<ErrorObjectResponse>>> handleConstraintViolation(
            final ConstraintViolationException ex
    ) {
        log.error("{} [handleConstraintViolation] Constraint violation error", LOGGER_PREFIX, ex);

        final List<ErrorObjectResponse> errors = ex.getConstraintViolations().stream()
                .map(violation -> new ErrorObjectResponse(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();

        return buildResponse(errors, ex.getClass());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseData<String>> handleBusinessException(BusinessException ex) {
        log.error("{} [handleBusinessException] Business error", LOGGER_PREFIX, ex);
        return buildResponse(ex.getMessage(), ex.getClass(), ex.getCode());
    }

    @ExceptionHandler({AuthenticationException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ResponseData<String>> handleSecurityException(Exception ex) {
        log.error("{} [handleSecurityException] Security error", LOGGER_PREFIX, ex);
        return buildResponse(null, ex.getClass(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData<String>> handleGeneral(Exception ex) {
        log.error("{} [handleGeneral] Unexpected error", LOGGER_PREFIX, ex);
        final HttpStatus status = HTTP_STATUS_BY_EXCEPTION.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(status)
                .body(new ResponseData<>("Error interno del servidor", status.value(), "INTERNAL_ERROR"));
    }

    private <T> ResponseEntity<ResponseData<T>> buildResponse(T errors, Class<? extends Exception> exceptionClass) {
        return buildResponse(errors, exceptionClass, null);
    }

    private <T> ResponseEntity<ResponseData<T>> buildResponse(T errors, Class<? extends Exception> exceptionClass, String customCode) {
        final HttpStatus status = HTTP_STATUS_BY_EXCEPTION.getOrDefault(exceptionClass, HttpStatus.INTERNAL_SERVER_ERROR);
        final ExceptionConfig config = EXCEPTION_CONFIG.get(exceptionClass);
        final String code = customCode != null ? customCode : (config != null ? config.code : "INTERNAL_ERROR");
        final T message = errors != null ? errors : (T) (config != null ? config.message : "Error interno");

        return ResponseEntity.status(status).body(new ResponseData<>(message, status.value(), code));
    }

    private record ExceptionConfig(String code, String message) {
    }
}