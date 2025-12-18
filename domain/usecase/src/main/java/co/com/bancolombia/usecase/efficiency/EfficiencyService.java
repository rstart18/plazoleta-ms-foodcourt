package co.com.bancolombia.usecase.efficiency;

import co.com.bancolombia.model.traceability.EmployeeRanking;

import java.util.List;

public interface EfficiencyService {
    List<EmployeeRanking> getEmployeesRanking(String userRole);
}