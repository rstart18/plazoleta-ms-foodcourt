package co.com.bancolombia.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEfficiencyResponse {
    private Long employeeId;
    private String employeeEmail;
    private Double averageDurationInMinutes;
    private Integer processedOrders;
}