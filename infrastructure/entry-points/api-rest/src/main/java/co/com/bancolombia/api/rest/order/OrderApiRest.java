package co.com.bancolombia.api.rest.order;

import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.dto.request.AssignOrderRequest;
import co.com.bancolombia.api.dto.request.OrderReadyRequest;
import co.com.bancolombia.api.dto.request.OrderRequest;
import co.com.bancolombia.api.dto.response.ApiResponse;
import co.com.bancolombia.api.dto.response.OrderResponse;
import co.com.bancolombia.api.mapper.dto.OrderMapper;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.usecase.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiRest {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            HttpServletRequest request) {

        Long clientId = JwtUserInterceptor.getUserId(request);
        String clientEmail = JwtUserInterceptor.getUserEmail(request);
        String clientPhone = JwtUserInterceptor.getUserPhone(request);
        String userRole = JwtUserInterceptor.getUserRole(request);
        Order order = orderMapper.toModel(orderRequest);
        Order createdOrder = orderService.createOrder(order, clientId, clientEmail, clientPhone, userRole);
        OrderResponse response = orderMapper.toResponseDTO(createdOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(response));
    }

    @PutMapping("/assign")
    public ResponseEntity<ApiResponse<OrderResponse>> assignOrderToEmployee(
            @RequestBody AssignOrderRequest request,
            HttpServletRequest httpRequest) {

        Long employeeId = JwtUserInterceptor.getUserId(httpRequest);
        String employeeEmail = JwtUserInterceptor.getUserEmail(httpRequest);
        String userRole = JwtUserInterceptor.getUserRole(httpRequest);
        String authToken = httpRequest.getHeader("Authorization");

        Order assignedOrder = orderService.assignOrderToEmployee(
                request.getOrderId(),
                employeeId,
                employeeEmail,
                userRole,
                authToken
        );

        OrderResponse orderResponse = orderMapper.toResponseDTO(assignedOrder);
        return ResponseEntity.ok(ApiResponse.of(orderResponse));
    }

    @PutMapping("/ready")
    public ResponseEntity<ApiResponse<OrderResponse>> markOrderAsReady(
            @RequestBody OrderReadyRequest request,
            HttpServletRequest httpRequest) {

        Long employeeId = JwtUserInterceptor.getUserId(httpRequest);
        String userRole = JwtUserInterceptor.getUserRole(httpRequest);
        String authToken = httpRequest.getHeader("Authorization");

        Order order = orderService.markOrderAsReady(request.getOrderId(), employeeId, userRole, authToken);
        OrderResponse orderResponse = orderMapper.toResponseDTO(order);

        return ResponseEntity.ok(ApiResponse.of(orderResponse));
    }
}
