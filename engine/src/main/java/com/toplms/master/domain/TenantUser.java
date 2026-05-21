package com.toplms.master.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * A login account that operates a tenant's workspace.
 *
 * <p><strong>Why this lives in {@code master/} and not {@code tenant/}:</strong>
 * login happens by email <em>before</em> we know which tenant the request
 * belongs to — so the lookup can't be tenant-scoped. The row carries a
 * {@code tenant_id} (which business it belongs to), but it is platform-managed
 * data, looked up outside any tenant context.
 *
 * <p>{@code tenantId} is a plain foreign-key column, not a JPA {@code @ManyToOne}
 * association — kept simple on purpose to avoid lazy-loading surprises while
 * you're still learning. We resolve the {@link Tenant} with a second explicit
 * query when needed.
 */
@Entity
@Table(name = "tenant_user")
@Getter
@Setter
@NoArgsConstructor
public class TenantUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * The user's role within their tenant (ADMIN, INSTRUCTOR, …). Many users
     * share one role → {@code @ManyToOne}, with the FK column {@code role_id}
     * on this table. Left nullable so {@code ddl-auto=update} can add the column
     * to any rows that existed before roles were introduced; new signups always
     * get one.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private TenantRole role;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    /** BCrypt hash — NEVER the plaintext password. Set by the provisioning service. */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
