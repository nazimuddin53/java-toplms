package com.toplms.master.domain;

/**
 * Lifecycle state of a {@link Tenant}.
 *
 * <ul>
 *   <li>{@code PROVISIONING} — being set up (reserved for future async setup).</li>
 *   <li>{@code ACTIVE} — operating normally; users can log in.</li>
 *   <li>{@code SUSPENDED} — disabled by a superadmin (e.g. non-payment); login blocked.</li>
 * </ul>
 */
public enum TenantStatus {
    PROVISIONING,
    ACTIVE,
    SUSPENDED
}
