package com.toplms.master.service;

/**
 * Immutable input to {@link TenantUserService#createUser}.
 *
 * <p>{@code tenantId} is supplied by the controller from the authenticated
 * admin's principal — never from the submitted form — so an admin can only
 * create users inside their own tenant.
 */
public record NewUserCommand(
        Long tenantId,
        String fullName,
        String email,
        String rawPassword,
        Long roleId
) {
}
