package com.toplms.master.service;

/**
 * Thrown when a chosen role does not belong to the tenant the user is being
 * created in — guards against form tampering (a submitted {@code roleId} that
 * points at another tenant's role).
 */
public class RoleNotInTenantException extends RuntimeException {

    public RoleNotInTenantException(Long roleId, Long tenantId) {
        super("Role " + roleId + " does not belong to tenant " + tenantId);
    }
}
