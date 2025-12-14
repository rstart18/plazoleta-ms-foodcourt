package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.request.OrderItemRequest;
import co.com.bancolombia.api.dto.request.OrderRequest;
import co.com.bancolombia.api.dto.response.OrderItemResponse;
import co.com.bancolombia.api.dto.response.OrderResponse;
import co.com.bancolombia.model.order.Order;
import co.com.bancolombia.model.orderitem.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toModel(OrderRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "plateName", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "subtotal", ignore = true)
    OrderItem toModel(OrderItemRequest dto);

    OrderResponse toResponseDTO(Order order);

    OrderItemResponse toResponseDTO(OrderItem orderItem);
}
