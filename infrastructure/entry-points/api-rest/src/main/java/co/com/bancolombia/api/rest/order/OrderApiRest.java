package co.com.bancolombia.api.rest.order;

import co.com.bancolombia.api.config.JwtUserInterceptor;
import co.com.bancolombia.api.dto.request.AssignOrderRequest;
import co.com.bancolombia.api.dto.request.CancelOrderRequest;
import co.com.bancolombia.api.dto.request.DeliverOrderRequest;
import co.com.bancolombia.api.dto.request.OrderReadyRequest;
import co.com.bancolombia.api.dto.request.OrderRequest;
import co.com.bancolombia.api.dto.response.ApiResponseData;
import co.com.bancolombia.api.dto.response.EmployeeEfficiencyResponse;
import co.com.bancolombia.api.dto.response.OrderResponse;
import co.com.bancolombia.api.dto.response.OrderTraceResponse;
import co.com.bancolombia.api.mapper.dto.EfficiencyMapper;
import co.com.bancolombia.api.mapper.dto.OrderMapper;
import co.com.bancolombia.api.mapper.dto.OrderTraceMapper;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.traceability.EmployeeRanking;
import co.com.bancolombia.model.traceability.OrderTrace;
import co.com.bancolombia.usecase.efficiency.EfficiencyService;
import co.com.bancolombia.usecase.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoints para gestión de órdenes y eficiencia de empleados")
public class OrderApiRest {

    private final OrderService orderService;
    private final EfficiencyService efficiencyService;
    private final OrderMapper orderMapper;
    private final EfficiencyMapper efficiencyMapper;
    private final OrderTraceMapper orderTraceMapper;

    @PostMapping
    public ResponseEntity<ApiResponseData<OrderResponse>> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            HttpServletRequest request) {

        Long clientId = JwtUserInterceptor.getUserId(request);
        String clientEmail = JwtUserInterceptor.getUserEmail(request);
        String clientPhone = JwtUserInterceptor.getUserPhone(request);
        String userRole = JwtUserInterceptor.getUserRole(request);
        Order order = orderMapper.toModel(orderRequest);
        Order createdOrder = orderService.createOrder(order, clientId, clientEmail, clientPhone, userRole);
        OrderResponse response = orderMapper.toResponseDTO(createdOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseData.of(response));
    }

    @PutMapping("/assign")
    public ResponseEntity<ApiResponseData<OrderResponse>> assignOrderToEmployee(
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
        return ResponseEntity.ok(ApiResponseData.of(orderResponse));
    }

    @PutMapping("/ready")
    public ResponseEntity<ApiResponseData<OrderResponse>> markOrderAsReady(
            @RequestBody OrderReadyRequest request,
            HttpServletRequest httpRequest) {

        Long employeeId = JwtUserInterceptor.getUserId(httpRequest);
        String userRole = JwtUserInterceptor.getUserRole(httpRequest);
        String authToken = httpRequest.getHeader("Authorization");

        Order order = orderService.markOrderAsReady(request.getOrderId(), employeeId, userRole, authToken);
        OrderResponse orderResponse = orderMapper.toResponseDTO(order);

        return ResponseEntity.ok(ApiResponseData.of(orderResponse));
    }

    @PutMapping("/deliver")
    public ResponseEntity<ApiResponseData<OrderResponse>> deliverOrder(
            @RequestBody DeliverOrderRequest request,
            HttpServletRequest httpRequest) {

        Long employeeId = JwtUserInterceptor.getUserId(httpRequest);
        String userRole = JwtUserInterceptor.getUserRole(httpRequest);
        String authToken = httpRequest.getHeader("Authorization");

        Order order = orderService.deliverOrder(
                request.getOrderId(),
                request.getSecurityPin(),
                employeeId,
                userRole,
                authToken
        );

        OrderResponse orderResponse = orderMapper.toResponseDTO(order);
        return ResponseEntity.ok(ApiResponseData.of(orderResponse));
    }

    @PutMapping("/cancel")
    public ResponseEntity<ApiResponseData<OrderResponse>> cancelOrder(
            @Valid @RequestBody CancelOrderRequest request,
            HttpServletRequest httpRequest) {

        Long clientId = JwtUserInterceptor.getUserId(httpRequest);
        String userRole = JwtUserInterceptor.getUserRole(httpRequest);

        Order order = orderService.cancelOrder(request.getOrderId(), clientId, userRole);
        OrderResponse orderResponse = orderMapper.toResponseDTO(order);

        return ResponseEntity.ok(ApiResponseData.of(orderResponse));
    }

    @GetMapping("/traces/{orderId}")
    public ResponseEntity<ApiResponseData<List<OrderTraceResponse>>> getOrderTraces(@PathVariable("orderId") Long orderId) {
        List<OrderTrace> orderTraces = orderService.getOrderTraces(orderId);
        List<OrderTraceResponse> traceResponses = orderTraceMapper.toResponseDTOList(orderTraces);
        return ResponseEntity.ok(ApiResponseData.of(traceResponses));
    }

    @GetMapping("/efficiency/employees/ranking")
    public ResponseEntity<ApiResponseData<List<EmployeeEfficiencyResponse>>> getEmployeesRanking(
            HttpServletRequest request) {

        String userRole = JwtUserInterceptor.getUserRole(request);
        List<EmployeeRanking> employeeRankings = efficiencyService.getEmployeesRanking(userRole);
        List<EmployeeEfficiencyResponse> response = efficiencyMapper.toResponseList(employeeRankings);

        return ResponseEntity.ok(ApiResponseData.of(response));
    }
}
