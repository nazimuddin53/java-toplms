package com.toplms.tenant.repository;

import com.toplms.tenant.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Spring Data JPA repository for {@link Course}, scoped per tenant. */
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTenant_Id(Long tenantId);
}
