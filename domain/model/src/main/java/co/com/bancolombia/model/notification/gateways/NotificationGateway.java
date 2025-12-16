package co.com.bancolombia.model.notification.gateways;

public interface NotificationGateway {
    void sendOrderReadySms(String phoneNumber, Long orderId, String securityPin);
}