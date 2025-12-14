package co.com.bancolombia.api.rest.order;

import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.constants.SecurityConstants;
import co.com.bancolombia.api.dto.request.OrderRequest;
import co.com.bancolombia.api.dto.response.OrderResponse;
import co.com.bancolombia.api.mapper.dto.OrderMapper;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.usecase.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderApiRest {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PreAuthorize(SecurityConstants.ROLE_CLIENT)
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            HttpServletRequest request) {

        Long customerId = JwtUserInterceptor.getUserId(request);
        Order order = orderMapper.toModel(orderRequest);
        Order createdOrder = orderService.createOrder(order, customerId);
        OrderResponse response = orderMapper.toResponseDTO(createdOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
