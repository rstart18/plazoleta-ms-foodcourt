package co.com.bancolombia.model.user.gateways;

public interface UserGateway {
    boolean hasOwnerRole(Long userId, String authToken);
}
