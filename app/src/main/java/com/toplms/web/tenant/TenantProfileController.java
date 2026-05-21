package com.toplms.web.tenant;

import com.toplms.security.TenantUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * The logged-in tenant's profile at {@code GET /app/profile}.
 *
 * <p>This route is NOT in SecurityConfig's permit list, so Spring Security
 * requires an authenticated session — an anonymous visitor is bounced to
 * {@code /login}. {@code @AuthenticationPrincipal} injects the
 * {@link TenantUserPrincipal} that {@code TenantUserDetailsService} built at
 * login, so we render the tenant's details with no extra database query.
 *
 * <p>(Per-tenant URLs like {@code /{slug}/...} and a tenant-resolution filter
 * come later; for now the profile reads everything it needs from the principal.)
 */
@Controller
public class TenantProfileController {

    @GetMapping("/app/profile")
    public String profile(@AuthenticationPrincipal TenantUserPrincipal principal, Model model) {
        model.addAttribute("tenantName", principal.getTenantName());
        model.addAttribute("slug", principal.getTenantSlug());
        model.addAttribute("fullName", principal.getFullName());
        model.addAttribute("email", principal.getUsername());
        model.addAttribute("roleName", principal.getRoleName());
        model.addAttribute("isAdmin", "ADMIN".equals(principal.getRoleName()));
        return "tenant/profile";
    }
}
