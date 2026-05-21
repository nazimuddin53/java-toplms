package com.toplms.tenant.service;

/**
 * Immutable input to {@link CourseService#create}. {@code tenantId} comes from
 * the authenticated admin's principal, never from the submitted form.
 */
public record NewCourseCommand(
        Long tenantId,
        String title,
        String description
) {
}
