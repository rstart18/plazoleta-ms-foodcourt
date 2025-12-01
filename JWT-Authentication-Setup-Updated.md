# JWT Authentication Setup para Microservicios

Este documento contiene todos los archivos necesarios para implementar validación JWT con roles en un microservicio Spring Boot.

## Configuración Requerida

### application.yaml
```yaml
jwt:
  secret: "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm"
  expiration: 86400000
```

## Dependencias Gradle

```gradle
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.springframework.boot:spring-boot-starter-oauth2-resource-server'
implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
implementation 'io.jsonwebtoken:jjwt-impl:0.11.5'
implementation 'io.jsonwebtoken:jjwt-jackson:0.11.5'
```

## Archivos de Configuración

### 1. SecurityConfig.java
```java
package co.com.bancolombia.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
        return http
                .authenticationProvider(authProvider)
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String roles = jwt.getClaimAsString("roles");
            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }

            return Arrays.stream(roles.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });

        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
```

### 2. GlobalExceptionHandler.java
```java
package co.com.bancolombia.api.config;

import co.com.bancolombia.api.dto.response.ErrorResponse;
import co.com.bancolombia.api.enums.ErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import io.micrometer.core.instrument.config.validate.ValidationException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthenticationException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.AUTHENTICATION_FAILED.getCode())
                .message(ErrorCode.AUTHENTICATION_FAILED.getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(org.springframework.security.authorization.AuthorizationDeniedException ex) {
        ErrorResponse error = ErrorResponse.builder()
                .code(ErrorCode.ACCESS_DENIED.getCode())
                .message(ErrorCode.ACCESS_DENIED.getDefaultMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message("Error de validación")
                .details(errors)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .code(ErrorCode.CONSTRAINT_VIOLATION.getCode())
                .message(ErrorCode.CONSTRAINT_VIOLATION.getDefaultMessage())
                .details(errors)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .code(ErrorCode.INTERNAL_ERROR.getCode())
                        .message(ErrorCode.INTERNAL_ERROR.getDefaultMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
```

## DTOs y Respuestas

### 3. ApiResponse.java
```java
package co.com.bancolombia.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private final T data;

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data);
    }
}
```

### 4. ErrorResponse.java
```java
package co.com.bancolombia.api.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<String> details;
}
```

### 5. ErrorCode.java
```java
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
```

### 6. SecurityConstants.java
```java
package co.com.bancolombia.api.constants;

public final class SecurityConstants {

    public static final String ROLE_ADMIN = "hasRole('ADMIN')";
    public static final String ROLE_OWNER = "hasRole('OWNER')";

    private SecurityConstants() {
    }
}
```

## Modelos de Dominio

### 7. User.java (Modelo de Usuario)
```java
package co.com.bancolombia.model.user;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String identityDocument;
    private String phone;
    private LocalDate birthDate;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 8. BusinessException.java
```java
package co.com.bancolombia.model.exception;

public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
```

## Uso en Controladores

```java
// Ejemplo de uso de @PreAuthorize
@PreAuthorize(SecurityConstants.ROLE_ADMIN)
public ResponseEntity<ApiResponse<String>> adminOnlyEndpoint() {
    return ResponseEntity.ok(ApiResponse.of("Solo admins"));
}
```

## Formato del Token JWT

El token JWT debe contener los roles en el claim "roles" separados por comas:

```json
{
  "sub": "user@example.com",
  "roles": "ROLE_ADMIN,ROLE_OWNER",
  "iat": 1638360000,
  "exp": 1638446400
}
```

## Uso del Token

### Headers de Autenticación
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Respuestas de Error

#### 401 - No Autenticado
```json
{
  "code": "AUTHENTICATION_FAILED",
  "message": "Credenciales inválidas",
  "timestamp": "2025-11-30T19:15:30"
}
```

#### 403 - Sin Permisos
```json
{
  "code": "ACCESS_DENIED",
  "message": "No tienes permisos para acceder a este recurso",
  "timestamp": "2025-11-30T19:15:30"
}
```

#### 400 - Validación
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Error de validación",
  "details": [
    "El email es obligatorio",
    "El teléfono debe contener máximo 13 caracteres"
  ],
  "timestamp": "2025-11-30T19:15:30"
}
```

## Notas Importantes

1. **Clave Secreta**: Debe ser la misma en todos los microservicios que validen el token
2. **Roles**: Los roles en el token deben incluir el prefijo "ROLE_" (ej: "ROLE_ADMIN")
3. **Expiración**: Los tokens tienen 24 horas de validez (86400000 ms)
4. **Debug**: Remover `debug = true` en producción del `@EnableWebSecurity`
5. **CORS**: Configurar CORS si es necesario para frontend
6. **HTTPS**: Usar HTTPS en producción para proteger los tokens

## Estructura de Carpetas por Capas (Clean Architecture)

### Applications Layer
```
applications/
└── app-service/
    └── src/main/java/co/com/bancolombia/
        └── config/
            └── UseCasesConfig.java
```

### Domain Layer
```
domain/
├── model/
│   └── src/main/java/co/com/bancolombia/model/
│       ├── user/
│       │   └── User.java
│       └── exception/
│           └── BusinessException.java
└── usecase/
    └── src/main/java/co/com/bancolombia/usecase/
        └── [casos de uso]
```

### Infrastructure Layer
```
infrastructure/
├── entry-points/
│   └── api-rest/
│       └── src/main/java/co/com/bancolombia/api/
│           ├── config/
│           │   ├── SecurityConfig.java
│           │   └── GlobalExceptionHandler.java
│           ├── constants/
│           │   └── SecurityConstants.java
│           ├── dto/
│           │   └── response/
│           │       ├── ApiResponse.java
│           │       └── ErrorResponse.java
│           ├── enums/
│           │   └── ErrorCode.java
│           └── rest/
│               └── [controladores]
└── driven-adapters/
    ├── jpa-repository/
    │   └── src/main/java/co/com/bancolombia/jpa/
    └── gateway/
        └── src/main/java/co/com/bancolombia/adapter/
```