package co.com.bancolombia.usecase.efficiency;

import co.com.bancolombia.model.traceability.EmployeeRanking;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import co.com.bancolombia.usecase.validator.RoleValidator;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EfficiencyUseCase implements EfficiencyService {

    private final TraceabilityGateway traceabilityGateway;

    @Override
    public List<EmployeeRanking> getEmployeesRanking(String userRole) {
        RoleValidator.validateOwnerRole(userRole);
        return traceabilityGateway.getEmployeesRanking();
    }
}