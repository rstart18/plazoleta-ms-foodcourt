package co.com.bancolombia.usecase.efficiency;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.traceability.EmployeeRanking;
import co.com.bancolombia.model.traceability.gateways.TraceabilityGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EfficiencyUseCaseTest {

    @Mock
    private TraceabilityGateway traceabilityGateway;

    private EfficiencyUseCase efficiencyUseCase;

    @BeforeEach
    void setUp() {
        efficiencyUseCase = new EfficiencyUseCase(traceabilityGateway);
    }

    @Test
    void getEmployeesRanking_ShouldReturnListWhenUserRoleIsOwner() {
        // Given
        String userRole = "OWNER";
        List<EmployeeRanking> expectedRanking = Arrays.asList(
                EmployeeRanking.builder()
                        .employeeId(13L)
                        .employeeEmail("carlos.villa@restaurant.com")
                        .averageDurationInMinutes(4.5)
                        .processedOrders(2)
                        .build(),
                EmployeeRanking.builder()
                        .employeeId(12L)
                        .employeeEmail("karla.nina@restaurant.com")
                        .averageDurationInMinutes(25.0)
                        .processedOrders(1)
                        .build(),
                EmployeeRanking.builder()
                        .employeeId(11L)
                        .employeeEmail("ana.armani@restaurant.com")
                        .averageDurationInMinutes(486.3333333333333)
                        .processedOrders(3)
                        .build()
        );

        when(traceabilityGateway.getEmployeesRanking()).thenReturn(expectedRanking);

        // When
        List<EmployeeRanking> result = efficiencyUseCase.getEmployeesRanking(userRole);

        // Then
        assertEquals(3, result.size());
        assertEquals(expectedRanking, result);
        assertEquals("carlos.villa@restaurant.com", result.get(0).getEmployeeEmail());
        assertEquals(13L, result.get(0).getEmployeeId());
        assertEquals(4.5, result.get(0).getAverageDurationInMinutes());
        assertEquals(2, result.get(0).getProcessedOrders());

        verify(traceabilityGateway).getEmployeesRanking();
    }

    @Test
    void getEmployeesRanking_ShouldThrowExceptionWhenUserRoleIsNotOwner() {
        // Given
        String userRole = "EMPLOYEE";

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> efficiencyUseCase.getEmployeesRanking(userRole));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        verify(traceabilityGateway, never()).getEmployeesRanking();
    }

    @Test
    void getEmployeesRanking_ShouldThrowExceptionWhenUserRoleIsClient() {
        // Given
        String userRole = "CLIENT";

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> efficiencyUseCase.getEmployeesRanking(userRole));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        verify(traceabilityGateway, never()).getEmployeesRanking();
    }

    @Test
    void getEmployeesRanking_ShouldThrowExceptionWhenUserRoleIsAdmin() {
        // Given
        String userRole = "ADMIN";

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> efficiencyUseCase.getEmployeesRanking(userRole));

        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
        verify(traceabilityGateway, never()).getEmployeesRanking();
    }

    @Test
    void getEmployeesRanking_ShouldReturnEmptyListWhenNoEmployeeData() {
        // Given
        String userRole = "OWNER";
        List<EmployeeRanking> emptyList = List.of();

        when(traceabilityGateway.getEmployeesRanking()).thenReturn(emptyList);

        // When
        List<EmployeeRanking> result = efficiencyUseCase.getEmployeesRanking(userRole);

        // Then
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());
        verify(traceabilityGateway).getEmployeesRanking();
    }

    @Test
    void getEmployeesRanking_ShouldReturnSingleEmployeeRanking() {
        // Given
        String userRole = "OWNER";
        EmployeeRanking singleEmployee = EmployeeRanking.builder()
                .employeeId(1L)
                .employeeEmail("single.employee@restaurant.com")
                .averageDurationInMinutes(10.5)
                .processedOrders(5)
                .build();
        List<EmployeeRanking> expectedRanking = List.of(singleEmployee);

        when(traceabilityGateway.getEmployeesRanking()).thenReturn(expectedRanking);

        // When
        List<EmployeeRanking> result = efficiencyUseCase.getEmployeesRanking(userRole);

        // Then
        assertEquals(1, result.size());
        assertEquals(singleEmployee, result.get(0));
        assertEquals("single.employee@restaurant.com", result.get(0).getEmployeeEmail());
        verify(traceabilityGateway).getEmployeesRanking();
    }

    @Test
    void getEmployeesRanking_ShouldCallGatewayOnlyOnce() {
        // Given
        String userRole = "OWNER";
        List<EmployeeRanking> expectedRanking = Arrays.asList(
                EmployeeRanking.builder()
                        .employeeId(1L)
                        .employeeEmail("employee1@restaurant.com")
                        .averageDurationInMinutes(15.0)
                        .processedOrders(3)
                        .build()
        );

        when(traceabilityGateway.getEmployeesRanking()).thenReturn(expectedRanking);

        // When
        List<EmployeeRanking> result = efficiencyUseCase.getEmployeesRanking(userRole);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(traceabilityGateway, times(1)).getEmployeesRanking();
    }
}