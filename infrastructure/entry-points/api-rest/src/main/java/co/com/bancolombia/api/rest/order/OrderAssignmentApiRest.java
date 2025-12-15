package co.com.bancolombia.api.rest.order;

import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.dto.request.AssignOrderRequest;
import co.com.bancolombia.api.dto.response.ApiResponse;
import co.com.bancolombia.api.dto.response.OrderResponse;
import co.com.bancolombia.api.mapper.dto.OrderMapper;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.usecase.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderAssignmentApiRest {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

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
}