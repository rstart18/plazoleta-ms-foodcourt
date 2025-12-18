package co.com.bancolombia.model.traceability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EmployeeRanking {
    private Long employeeId;
    private String employeeEmail;
    private Double averageDurationInMinutes;
    private Integer processedOrders;
}