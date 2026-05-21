package com.toplms.master.repository;

import com.toplms.master.domain.TenantUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link TenantUser}.
 *
 * <p>{@code findByEmail} is what the login flow uses to look up a user by the
 * email they typed; {@code existsByEmail} guards signup against duplicates;
 * {@code findByTenant_Id} lists every user in one tenant for the admin dashboard.
 */
public interface TenantUserRepository extends JpaRepository<TenantUser, Long> {

    boolean existsByEmail(String email);

    Optional<TenantUser> findByEmail(String email);

    List<TenantUser> findByTenant_Id(Long tenantId);

    /**
     * Users in a tenant filtered by role name — powers the Teachers (INSTRUCTOR)
     * and Students (LEARNER) views. The path {@code Tenant_Id} steps into the
     * tenant association; {@code Role_RoleName} steps into the role association
     * and matches its {@code roleName}.
     */
    List<TenantUser> findByTenant_IdAndRole_RoleName(Long tenantId, String roleName);
}
