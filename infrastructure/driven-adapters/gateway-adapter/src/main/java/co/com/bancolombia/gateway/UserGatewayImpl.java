package co.com.bancolombia.gateway;

import co.com.bancolombia.gateway.dto.UserApiResponse;
import co.com.bancolombia.gateway.dto.UserRoleResponse;
import co.com.bancolombia.model.user.gateways.UserGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class UserGatewayImpl implements UserGateway {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserGatewayImpl(RestTemplate restTemplate,
                           @Value("${services.user.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public boolean hasOwnerRole(Long userId, String authToken) {
        try {
            String url = userServiceUrl + "/api/users/" + userId + "/roles";
            log.info("Calling user service: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<UserApiResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, UserApiResponse.class);

            UserApiResponse apiResponse = response.getBody();

            if (apiResponse != null && apiResponse.getData() != null && apiResponse.getData().getRoles() != null) {
                log.info("User roles: {}", apiResponse.getData().getRoles());
                boolean hasOwnerRole = apiResponse.getData().getRoles().contains("OWNER");
                log.info("Has OWNER role: {}", hasOwnerRole);
                return hasOwnerRole;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}