package co.com.bancolombia.usecase.validator;

import co.com.bancolombia.model.enums.DomainErrorCode;
import co.com.bancolombia.model.enums.UserRole;
import co.com.bancolombia.model.exception.BusinessException;

public class RoleValidator {

    public static void validateAdminRole(String userRole) {
        if (!UserRole.ADMIN.name().equals(userRole)) {
            throw new BusinessException(
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(),
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getMessage()
            );
        }
    }

    public static void validateOwnerRole(String userRole) {
        if (!UserRole.OWNER.name().equals(userRole)) {
            throw new BusinessException(
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(),
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getMessage()
            );
        }
    }

    public static void validateEmployeeRole(String userRole) {
        if (!UserRole.EMPLOYEE.name().equals(userRole)) {
            throw new BusinessException(
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(),
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getMessage()
            );
        }
    }

    public static void validateClientRole(String userRole) {
        if (!UserRole.CLIENT.name().equals(userRole)) {
            throw new BusinessException(
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getCode(),
                    DomainErrorCode.INSUFFICIENT_PERMISSIONS.getMessage()
            );
        }
    }
}