package co.com.bancolombia.api.mapper;

import co.com.bancolombia.api.dto.response.OrderAssignedResponse;
import co.com.bancolombia.model.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = OrderItemMapper.class)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);
    
    OrderAssignedResponse toDto(Order order);
}