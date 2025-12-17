package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.response.OrderTraceResponse;
import co.com.bancolombia.model.traceability.OrderTrace;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderTraceMapper {

    OrderTraceResponse toResponseDTO(OrderTrace orderTrace);

    List<OrderTraceResponse> toResponseDTOList(List<OrderTrace> orderTraces);
}