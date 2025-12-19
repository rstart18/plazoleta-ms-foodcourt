package co.com.bancolombia.usecase.validator;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.enums.UserRole;
import co.com.bancolombia.model.exception.BusinessException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleValidatorTest {

    @Test
    void shouldValidateAdminRoleSuccessfully() {
        // When & Then - no exception should be thrown
        assertDoesNotThrow(() -> RoleValidator.validateAdminRole(UserRole.ADMIN.name()));
    }

    @Test
    void shouldThrowExceptionWhenRoleIsNotAdmin() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateAdminRole(UserRole.OWNER.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenRoleIsEmployee() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateAdminRole(UserRole.EMPLOYEE.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionWhenRoleIsClient() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateAdminRole(UserRole.CLIENT.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateOwnerRoleSuccessfully() {
        // When & Then - no exception should be thrown
        assertDoesNotThrow(() -> RoleValidator.validateOwnerRole(UserRole.OWNER.name()));
    }

    @Test
    void shouldThrowExceptionWhenRoleIsNotOwner() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateOwnerRole(UserRole.ADMIN.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionForEmployeeWhenOwnerRequired() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateOwnerRole(UserRole.EMPLOYEE.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionForClientWhenOwnerRequired() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateOwnerRole(UserRole.CLIENT.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateEmployeeRoleSuccessfully() {
        // When & Then - no exception should be thrown
        assertDoesNotThrow(() -> RoleValidator.validateEmployeeRole(UserRole.EMPLOYEE.name()));
    }

    @Test
    void shouldThrowExceptionWhenRoleIsNotEmployee() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateEmployeeRole(UserRole.OWNER.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionForAdminWhenEmployeeRequired() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateEmployeeRole(UserRole.ADMIN.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionForClientWhenEmployeeRequired() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateEmployeeRole(UserRole.CLIENT.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldValidateClientRoleSuccessfully() {
        // When & Then - no exception should be thrown
        assertDoesNotThrow(() -> RoleValidator.validateClientRole(UserRole.CLIENT.name()));
    }

    @Test
    void shouldThrowExceptionWhenRoleIsNotClient() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateClientRole(UserRole.OWNER.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionForAdminWhenClientRequired() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateClientRole(UserRole.ADMIN.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionForEmployeeWhenClientRequired() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateClientRole(UserRole.EMPLOYEE.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }

    @Test
    void shouldThrowExceptionForOwnerWhenClientRequired() {
        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> RoleValidator.validateClientRole(UserRole.OWNER.name()));
        assertEquals(DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(), exception.getCode());
    }
}