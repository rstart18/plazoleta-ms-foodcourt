package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.response.OrderItemResponse;
import co.com.bancolombia.model.orderitem.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderItemMapper {
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);
    
    @Mapping(target = "unitPrice", expression = "java(orderItem.getUnitPrice().doubleValue())")
    @Mapping(target = "subtotal", expression = "java(orderItem.getSubtotal().doubleValue())")
    OrderItemResponse toDto(OrderItem orderItem);
}