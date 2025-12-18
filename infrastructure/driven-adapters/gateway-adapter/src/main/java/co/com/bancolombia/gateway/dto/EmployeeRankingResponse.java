package co.com.bancolombia.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRankingResponse {
    private Long employeeId;
    private String employeeEmail;
    private Double averageDurationInMinutes;
    private Integer processedOrders;
}