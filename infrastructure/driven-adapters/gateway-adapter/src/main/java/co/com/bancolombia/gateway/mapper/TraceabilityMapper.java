package co.com.bancolombia.gateway.mapper;

import co.com.bancolombia.gateway.dto.EmployeeRankingResponse;
import co.com.bancolombia.gateway.dto.TraceabilityResponse;
import co.com.bancolombia.model.traceability.EmployeeRanking;
import co.com.bancolombia.model.traceability.OrderTrace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TraceabilityMapper {

    @Mapping(target = "id", ignore = true)
    OrderTrace toModel(TraceabilityResponse traceabilityResponse);

    List<OrderTrace> toModelList(List<TraceabilityResponse> traceabilityResponses);

    EmployeeRanking toModel(EmployeeRankingResponse employeeRankingResponse);

    List<EmployeeRanking> toEmployeeRankingList(List<EmployeeRankingResponse> employeeRankingResponses);
}