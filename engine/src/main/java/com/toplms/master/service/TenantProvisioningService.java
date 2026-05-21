package com.toplms.master.service;

import com.toplms.master.domain.Tenant;
import com.toplms.master.domain.TenantRole;
import com.toplms.master.domain.TenantStatus;
import com.toplms.master.domain.TenantUser;
import com.toplms.master.repository.TenantRoleRepository;
import com.toplms.master.repository.TenantUserRepository;
import com.toplms.master.repository.TenantRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates a tenant, its default roles, and its first admin login account — the
 * business logic behind public signup.
 *
 * <p>{@code @Service} marks this as a Spring-managed bean (picked up by the
 * component scan rooted at {@code com.toplms}). Dependencies are injected
 * through the constructor — the modern, testable form of dependency injection.
 *
 * <p>{@code @Transactional} on {@link #provision} wraps the whole method in ONE
 * database transaction: the tenant, its roles, and the admin user all commit
 * together, or none do. You can never end up with a tenant that has no roles or
 * no way to log in.
 */
@Service
public class TenantProvisioningService {

    /** Role name granted to the person who signs the tenant up. */
    public static final String ADMIN_ROLE = "ADMIN";

    private final TenantRepository tenants;
    private final TenantUserRepository adminUsers;
    private final TenantRoleRepository roles;
    private final PasswordEncoder passwordEncoder;

    public TenantProvisioningService(TenantRepository tenants,
                                     TenantUserRepository adminUsers,
                                     TenantRoleRepository roles,
                                     PasswordEncoder passwordEncoder) {
        this.tenants = tenants;
        this.adminUsers = adminUsers;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Provisions a new tenant + its default roles + the founding admin user.
     *
     * @return the persisted {@link Tenant} (now with its generated id)
     * @throws SlugAlreadyTakenException  if the slug is in use
     * @throws EmailAlreadyTakenException if the email is in use
     */
    @Transactional
    public Tenant provision(NewTenantCommand cmd) {
        String slug = cmd.slug().toLowerCase();

        if (tenants.existsBySlug(slug)) {
            throw new SlugAlreadyTakenException(slug);
        }
        if (adminUsers.existsByEmail(cmd.adminEmail())) {
            throw new EmailAlreadyTakenException(cmd.adminEmail());
        }

        Tenant tenant = new Tenant();
        tenant.setBusinessName(cmd.businessName());
        tenant.setSlug(slug);
        tenant.setStatus(TenantStatus.ACTIVE);
        Tenant saved = tenants.save(tenant);

        // Every new workspace gets a standard set of roles. The founder becomes
        // the ADMIN; the others exist so they can be assigned to future users.
        TenantRole adminRole = createRole(saved, ADMIN_ROLE, "Full access to the workspace and its settings");
        createRole(saved, "INSTRUCTOR", "Creates and manages courses and assessments");
        createRole(saved, "LEARNER", "Enrolls in courses and tracks progress");

        TenantUser admin = new TenantUser();
        admin.setTenant(saved);
        admin.setRole(adminRole);
        admin.setFullName(cmd.adminFullName());
        admin.setEmail(cmd.adminEmail());
        // Hash here — the plaintext from the command never touches the database.
        admin.setPasswordHash(passwordEncoder.encode(cmd.adminRawPassword()));
        adminUsers.save(admin);

        return saved;
    }

    private TenantRole createRole(Tenant tenant, String name, String description) {
        TenantRole role = new TenantRole();
        role.setTenant(tenant);
        role.setRoleName(name);
        role.setDescription(description);
        role.setMenu_json("[]");   // column is NOT NULL; empty JSON array as a placeholder
        return roles.save(role);
    }
}
