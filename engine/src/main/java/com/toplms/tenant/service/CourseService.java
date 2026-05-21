package com.toplms.tenant.service;

import com.toplms.master.domain.Tenant;
import com.toplms.master.repository.TenantRepository;
import com.toplms.tenant.domain.Course;
import com.toplms.tenant.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Business logic for managing a tenant's courses. */
@Service
public class CourseService {

    private final CourseRepository courses;
    private final TenantRepository tenants;

    public CourseService(CourseRepository courses, TenantRepository tenants) {
        this.courses = courses;
        this.tenants = tenants;
    }

    @Transactional
    public Course create(NewCourseCommand cmd) {
        Tenant tenant = tenants.findById(cmd.tenantId())
                .orElseThrow(() -> new IllegalArgumentException("Unknown tenant: " + cmd.tenantId()));

        Course course = new Course();
        course.setTenant(tenant);
        course.setTitle(cmd.title());
        course.setDescription(cmd.description());
        return courses.save(course);
    }

    @Transactional(readOnly = true)
    public List<Course> listForTenant(Long tenantId) {
        return courses.findByTenant_Id(tenantId);
    }
}
