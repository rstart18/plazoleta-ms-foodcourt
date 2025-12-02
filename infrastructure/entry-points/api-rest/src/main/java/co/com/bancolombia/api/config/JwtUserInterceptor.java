package co.com.bancolombia.api.config;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;
import java.util.Map;

@Component
public class JwtUserInterceptor implements HandlerInterceptor {

    private static final String USER_ID_ATTRIBUTE = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            Long userId = extractUserIdFromToken(authHeader);
            request.setAttribute(USER_ID_ATTRIBUTE, userId);
        }
        return true;
    }

    private Long extractUserIdFromToken(String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");
            String[] parts = token.split("\\.");

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claims = mapper.readValue(payload, Map.class);

            Number userId = (Number) claims.get("userId");
            if (userId == null) {
                throw new BusinessException(
                        DomainErrorCode.INVALID_TOKEN.getCode(),
                        "Token JWT no contiene userId"
                );
            }

            return userId.longValue();
        } catch (Exception e) {
            throw new BusinessException(
                    DomainErrorCode.INVALID_TOKEN.getCode(),
                    "Error al procesar token JWT"
            );
        }
    }

    public static Long getUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(USER_ID_ATTRIBUTE);
    }
}
