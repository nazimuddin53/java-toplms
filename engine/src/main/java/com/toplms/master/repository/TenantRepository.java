package com.toplms.master.repository;

import com.toplms.master.domain.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Tenant}.
 *
 * <p>You write an <em>interface</em> and Spring Data generates the implementation
 * at runtime. Extending {@code JpaRepository<Tenant, Long>} gives you
 * {@code save}, {@code findById}, {@code findAll}, {@code delete}, etc. for free
 * (Long = the type of the {@code @Id}).
 *
 * <p>The two methods below are <em>derived queries</em>: Spring parses the method
 * name and writes the SQL. {@code existsBySlug} →
 * {@code SELECT count(*) > 0 ... WHERE slug = ?}; {@code findBySlug} →
 * {@code SELECT ... WHERE slug = ?}. No implementation needed.
 */
public interface TenantRepository extends JpaRepository<Tenant, Long> {

    boolean existsBySlug(String slug);

    Optional<Tenant> findBySlug(String slug);
}
