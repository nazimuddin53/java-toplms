package com.toplms.master.service;

import com.toplms.master.domain.Tenant;
import com.toplms.master.domain.TenantRole;
import com.toplms.master.domain.TenantUser;
import com.toplms.master.repository.TenantRepository;
import com.toplms.master.repository.TenantRoleRepository;
import com.toplms.master.repository.TenantUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Adds users to an <em>existing</em> tenant (admin "create user" flow). Distinct
 * from {@link TenantProvisioningService}, which stands up a whole new tenant.
 */
@Service
public class TenantUserService {

    private final TenantUserRepository users;
    private final TenantRepository tenants;
    private final TenantRoleRepository roles;
    private final PasswordEncoder passwordEncoder;

    public TenantUserService(TenantUserRepository users,
                             TenantRepository tenants,
                             TenantRoleRepository roles,
                             PasswordEncoder passwordEncoder) {
        this.users = users;
        this.tenants = tenants;
        this.roles = roles;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TenantUser createUser(NewUserCommand cmd) {
        if (users.existsByEmail(cmd.email())) {
            throw new EmailAlreadyTakenException(cmd.email());
        }

        Tenant tenant = tenants.findById(cmd.tenantId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown tenant: " + cmd.tenantId()));

        TenantRole role = roles.findById(cmd.roleId())
                .orElseThrow(() -> new RoleNotInTenantException(cmd.roleId(), cmd.tenantId()));

        // Security: don't trust the submitted roleId — the role must belong to
        // this admin's own tenant, otherwise one tenant could attach its users
        // to another tenant's role.
        if (!role.getTenant().getId().equals(tenant.getId())) {
            throw new RoleNotInTenantException(cmd.roleId(), cmd.tenantId());
        }

        TenantUser user = new TenantUser();
        user.setTenant(tenant);
        user.setRole(role);
        user.setFullName(cmd.fullName());
        user.setEmail(cmd.email());
        user.setPasswordHash(passwordEncoder.encode(cmd.rawPassword()));
        return users.save(user);
    }
}
