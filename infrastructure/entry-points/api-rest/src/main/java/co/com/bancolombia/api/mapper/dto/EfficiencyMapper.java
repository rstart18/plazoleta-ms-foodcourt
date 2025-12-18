package co.com.bancolombia.api.mapper.dto;

import co.com.bancolombia.api.dto.response.EmployeeEfficiencyResponse;
import co.com.bancolombia.api.dto.response.EmployeeRankingListResponse;
import co.com.bancolombia.model.traceability.EmployeeRanking;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EfficiencyMapper {

    EmployeeEfficiencyResponse toResponse(EmployeeRanking employeeRanking);

    List<EmployeeEfficiencyResponse> toResponseList(List<EmployeeRanking> employeeRankings);

    default EmployeeRankingListResponse toRankingListResponse(List<EmployeeRanking> employeeRankings) {
        return EmployeeRankingListResponse.builder()
                .data(toResponseList(employeeRankings))
                .build();
    }
}