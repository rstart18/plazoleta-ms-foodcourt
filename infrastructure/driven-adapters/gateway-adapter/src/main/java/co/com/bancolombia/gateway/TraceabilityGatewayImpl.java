package co.com.bancolombia.gateway;

import co.com.bancolombia.gateway.dto.TraceabilityRequest;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TraceabilityGatewayImpl implements TraceabilityGateway {

    private static final String TRACES_ENDPOINT = "/api/orders/traces";

    private final RestTemplate restTemplate;
    private final String traceabilityServiceUrl;

    public TraceabilityGatewayImpl(RestTemplate restTemplate,
                                   @Value("${services.traceability.url}") String traceabilityServiceUrl) {
        this.restTemplate = restTemplate;
        this.traceabilityServiceUrl = traceabilityServiceUrl;
    }

    @Override
    public void sendOrderStatusChange(Long orderId, Long clientId, String clientEmail,
                                    OrderStatus previousStatus, OrderStatus newStatus,
                                    Long employeeId, String employeeEmail) {
        try {
            String url = traceabilityServiceUrl + TRACES_ENDPOINT;
            log.info("Sending traceability event to: {}", url);

            TraceabilityRequest request = TraceabilityRequest.builder()
                    .orderId(orderId)
                    .clientId(clientId)
                    .clientEmail(clientEmail)
                    .previousStatus(previousStatus)
                    .newStatus(newStatus)
                    .employeeId(employeeId)
                    .employeeEmail(employeeEmail)
                    .timestamp(LocalDateTime.now())
                    .build();

            log.info("Traceability request payload: {}", request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<TraceabilityRequest> entity = new HttpEntity<>(request, headers);

            var response = restTemplate.postForEntity(url, entity, String.class);
            log.info("Traceability response: status={}, body={}", response.getStatusCode(), response.getBody());

        } catch (Exception e) {
            log.error("Error sending traceability event for orderId {}: {}", orderId, e.getMessage(), e);
        }
    }
}