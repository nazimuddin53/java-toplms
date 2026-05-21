package com.toplms.web.tenant;

import com.toplms.master.repository.TenantRoleRepository;
import com.toplms.master.repository.TenantUserRepository;
import com.toplms.master.service.EmailAlreadyTakenException;
import com.toplms.master.service.NewUserCommand;
import com.toplms.master.service.RoleNotInTenantException;
import com.toplms.master.service.TenantUserService;
import com.toplms.security.TenantUserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin user management under {@code /app/admin/users}.
 *
 * <ul>
 *   <li>{@code GET  /app/admin/users}      — list this tenant's users</li>
 *   <li>{@code GET  /app/admin/users/new}  — show the create-user form</li>
 *   <li>{@code POST /app/admin/users}      — create a user, then redirect to the list</li>
 * </ul>
 *
 * <p>The class-level {@code @ModelAttribute} method runs before every handler
 * and supplies {@code tenantName} (used by the shared sidebar fragment), so each
 * handler doesn't repeat it.
 */
@Controller
@RequestMapping("/app/admin/users")
public class AdminUserController {

    private final TenantUserService userService;
    private final TenantUserRepository users;
    private final TenantRoleRepository roles;

    public AdminUserController(TenantUserService userService,
                               TenantUserRepository users,
                               TenantRoleRepository roles) {
        this.userService = userService;
        this.users = users;
        this.roles = roles;
    }

    @ModelAttribute
    public void common(@AuthenticationPrincipal TenantUserPrincipal principal, Model model) {
        if (principal != null) {
            model.addAttribute("tenantName", principal.getTenantName());
        }
    }

    @GetMapping
    public String list(@AuthenticationPrincipal TenantUserPrincipal principal, Model model) {
        model.addAttribute("users", users.findByTenant_Id(principal.getTenantId()));
        return "tenant/admin-users";
    }

    @GetMapping("/new")
    public String newForm(@AuthenticationPrincipal TenantUserPrincipal principal, Model model) {
        if (!model.containsAttribute("createUserForm")) {
            model.addAttribute("createUserForm", new CreateUserForm());
        }
        model.addAttribute("roles", roles.findByTenant_Id(principal.getTenantId()));
        return "tenant/admin-new-user";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("createUserForm") CreateUserForm form,
                         BindingResult binding,
                         @AuthenticationPrincipal TenantUserPrincipal principal,
                         Model model,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            model.addAttribute("roles", roles.findByTenant_Id(principal.getTenantId()));
            return "tenant/admin-new-user";
        }
        try {
            userService.createUser(new NewUserCommand(
                    principal.getTenantId(),
                    form.getFullName(),
                    form.getEmail(),
                    form.getPassword(),
                    form.getRoleId()));
        } catch (EmailAlreadyTakenException e) {
            binding.rejectValue("email", "email.taken", "An account with that email already exists");
            model.addAttribute("roles", roles.findByTenant_Id(principal.getTenantId()));
            return "tenant/admin-new-user";
        } catch (RoleNotInTenantException e) {
            binding.rejectValue("roleId", "role.invalid", "Please choose a valid role");
            model.addAttribute("roles", roles.findByTenant_Id(principal.getTenantId()));
            return "tenant/admin-new-user";
        }
        ra.addFlashAttribute("created", form.getEmail());
        return "redirect:/app/admin/users";
    }
}
