package com.toplms.master.service;

/**
 * Immutable input to {@link TenantProvisioningService#provision}.
 *
 * <p>This is the domain-shaped "command" — the boundary object between the web
 * layer and the business layer. The web {@code SignupForm} (in {@code app/}) is
 * translated into one of these by the controller, so the service never sees
 * HTTP-specific concerns. Any other caller (admin panel, CLI, API) can build a
 * command too, without going through the public signup form.
 *
 * <p>A {@code record} gives us a concise, immutable carrier with a canonical
 * constructor and accessors ({@code businessName()}, {@code slug()}, …).
 */
public record NewTenantCommand(
        String businessName,
        String slug,
        String adminFullName,
        String adminEmail,
        String adminRawPassword
) {
}
