package com.toplms.master.repository;

import com.toplms.master.domain.TenantRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link TenantRole}.
 *
 * <p>The method names traverse the {@code tenant} association: {@code findByTenant_Id}
 * becomes {@code WHERE role.tenant_id = ?}. The underscore tells Spring Data to
 * step into the {@code tenant} property and match its {@code id} — rather than
 * looking for a (non-existent) {@code tenantId} property on TenantRole.
 */
public interface TenantRoleRepository extends JpaRepository<TenantRole, Long> {

    List<TenantRole> findByTenant_Id(Long tenantId);

    Optional<TenantRole> findByTenant_IdAndRoleName(Long tenantId, String roleName);
}
