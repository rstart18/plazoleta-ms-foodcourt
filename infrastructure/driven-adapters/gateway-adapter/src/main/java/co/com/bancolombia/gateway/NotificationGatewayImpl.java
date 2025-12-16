package co.com.bancolombia.gateway;

import co.com.bancolombia.gateway.dto.SmsRequest;
import co.com.bancolombia.model.notification.gateways.NotificationGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class NotificationGatewayImpl implements NotificationGateway {

    private static final String SMS_ENDPOINT = "/api/notifications/sms";

    private final RestTemplate restTemplate;
    private final String notificationServiceUrl;

    public NotificationGatewayImpl(RestTemplate restTemplate,
                                   @Value("${services.notification.url}") String notificationServiceUrl) {
        this.restTemplate = restTemplate;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    @Override
    public void sendOrderReadySms(String phoneNumber, Long orderId, String securityPin) {
        try {
            String url = notificationServiceUrl + SMS_ENDPOINT;
            String message = String.format("Tu orden #%d esta lista para que la tomes. PIN de Seguridad: %s", orderId, securityPin);
            
            SmsRequest request = SmsRequest.builder()
                    .phoneNumber(phoneNumber)
                    .message(message)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SmsRequest> entity = new HttpEntity<>(request, headers);

            restTemplate.postForEntity(url, entity, Void.class);
            log.info("SMS sent successfully for orderId: {}", orderId);

        } catch (Exception e) {
            log.error("Error sending SMS for orderId {}: {}", orderId, e.getMessage(), e);
        }
    }
}