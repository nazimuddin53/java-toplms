package com.toplms.web.tenant;

import com.toplms.master.repository.TenantUserRepository;
import com.toplms.security.TenantUserPrincipal;
import com.toplms.tenant.service.CourseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Admin dashboard overview at {@code GET /app/admin} (ROLE_ADMIN). Shows headline
 * counts for the tenant: courses, teachers (INSTRUCTOR), students (LEARNER).
 */
@Controller
public class AdminDashboardController {

    private final TenantUserRepository users;
    private final CourseService courseService;

    public AdminDashboardController(TenantUserRepository users, CourseService courseService) {
        this.users = users;
        this.courseService = courseService;
    }

    @GetMapping("/app/admin")
    public String dashboard(@AuthenticationPrincipal TenantUserPrincipal principal, Model model) {
        Long tenantId = principal.getTenantId();
        model.addAttribute("tenantName", principal.getTenantName());
        model.addAttribute("fullName", principal.getFullName());
        model.addAttribute("roleName", principal.getRoleName());
        model.addAttribute("slug", principal.getTenantSlug());
        model.addAttribute("courseCount", courseService.listForTenant(tenantId).size());
        model.addAttribute("teacherCount", users.findByTenant_IdAndRole_RoleName(tenantId, "INSTRUCTOR").size());
        model.addAttribute("studentCount", users.findByTenant_IdAndRole_RoleName(tenantId, "LEARNER").size());
        return "tenant/admin-dashboard";
    }
}
