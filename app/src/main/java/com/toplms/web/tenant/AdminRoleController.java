package com.toplms.web.tenant;

import com.toplms.master.repository.TenantRoleRepository;
import com.toplms.security.TenantUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Admin roles view at {@code GET /app/admin/roles} — lists the roles defined for
 * this tenant (seeded at signup: ADMIN, INSTRUCTOR, LEARNER).
 */
@Controller
public class AdminRoleController {

    private final TenantRoleRepository roles;

    public AdminRoleController(TenantRoleRepository roles) {
        this.roles = roles;
    }

    @GetMapping("/app/admin/roles")
    public String list(@AuthenticationPrincipal TenantUserPrincipal principal, Model model) {
        model.addAttribute("tenantName", principal.getTenantName());
        model.addAttribute("roles", roles.findByTenant_Id(principal.getTenantId()));
        return "tenant/admin-roles";
    }
}
