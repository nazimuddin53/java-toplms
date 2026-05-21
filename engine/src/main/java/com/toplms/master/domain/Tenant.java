package com.toplms.master.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * A tenant = one customer business on the platform. Control-plane data: it has
 * NO {@code tenant_id} column because it IS the tenant. Lives in the shared
 * database alongside every other tenant's row.
 *
 * <p>{@code @Entity} marks this class as a JPA-managed persistent type — Hibernate
 * maps it to the {@code tenant} table ({@code @Table}). {@code @NoArgsConstructor}
 * is required: JPA instantiates entities reflectively via a no-arg constructor.
 *
 * <p>We use Lombok {@code @Getter}/{@code @Setter} but deliberately NOT
 * {@code @Data} — {@code @Data} also generates {@code equals}/{@code hashCode}
 * over all fields, which misbehaves for JPA entities (the generated id is null
 * before persist, then changes after).
 */
@Entity
@Table(name = "tenant")
@Getter
@Setter
@NoArgsConstructor
public class Tenant {

    /**
     * {@code IDENTITY} = let PostgreSQL assign the id via a
     * {@code BIGINT GENERATED AS IDENTITY} column. Simplest strategy; Hibernate
     * reads the generated value back after the INSERT.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_name", nullable = false, length = 120)
    private String businessName;

    /** URL-safe tenant key (e.g. {@code acme}) — unique across the platform. */
    @Column(nullable = false, unique = true, length = 40)
    private String slug;

    /**
     * {@code @Enumerated(STRING)} stores the enum by name ({@code "ACTIVE"})
     * rather than its ordinal (0, 1, 2). Always prefer STRING — ORDINAL breaks
     * silently if anyone reorders the enum constants later.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TenantStatus status;

    /** {@code @CreationTimestamp} (Hibernate) sets this once, on first INSERT. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
