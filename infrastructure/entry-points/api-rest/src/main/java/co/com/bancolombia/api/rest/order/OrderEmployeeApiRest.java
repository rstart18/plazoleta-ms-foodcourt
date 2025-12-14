package co.com.bancolombia.api.rest.order;

import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.dto.response.ApiResponse;
import co.com.bancolombia.api.dto.response.PagedOrderResponse;
import co.com.bancolombia.api.mapper.dto.OrderMapper;
import co.com.bancolombia.model.enums.OrderStatus;
import co.com.bancolombia.model.page.PagedResult;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.usecase.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/employee")
@RequiredArgsConstructor
public class OrderEmployeeApiRest {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedOrderResponse>> listOrdersByStatus(
            @RequestParam("status") OrderStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            HttpServletRequest request) {

        Long employeeId = JwtUserInterceptor.getUserId(request);
        String userRole = JwtUserInterceptor.getUserRole(request);
        String authToken = request.getHeader("Authorization");
        
        PagedResult<Order> orders = orderService.listOrdersByStatus(status, page, size, employeeId, userRole, authToken);
        PagedOrderResponse response = orderMapper.toPagedResponse(orders);

        return ResponseEntity.ok(ApiResponse.of(response));
    }
}