package co.com.bancolombia.gateway;

import co.com.bancolombia.gateway.dto.TraceabilityApiResponse;
import co.com.bancolombia.gateway.dto.TraceabilityRequest;
import co.com.bancolombia.gateway.dto.TraceabilityResponse;
import co.com.bancolombia.gateway.mapper.TraceabilityMapper;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.traceability.OrderTrace;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TraceabilityGatewayImpl implements TraceabilityGateway {

    private static final String TRACES_ENDPOINT = "/api/orders/traces";

    private final RestTemplate restTemplate;
    private final TraceabilityMapper traceabilityMapper;

    @Value("${services.traceability.url}")
    private String traceabilityServiceUrl;

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

    @Override
    public List<OrderTrace> getOrderTraces(Long orderId) {
        try {
            String url = traceabilityServiceUrl + TRACES_ENDPOINT + "/" + orderId;
            log.info("Getting traceability events from: {}", url);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            var response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    TraceabilityApiResponse.class
            );

            log.info("Traceability get response: status={}", response.getStatusCode());
            TraceabilityApiResponse apiResponse = response.getBody();

            if (apiResponse == null || apiResponse.getData() == null) {
                return List.of();
            }

            return traceabilityMapper.toModelList(apiResponse.getData());

        } catch (Exception e) {
            log.error("Error getting traceability events for orderId {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException("Error getting order traces: " + e.getMessage(), e);
        }
    }
}