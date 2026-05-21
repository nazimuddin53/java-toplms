package com.toplms.web.tenant;

import com.toplms.master.domain.TenantRole;
import com.toplms.master.repository.TenantRoleRepository;
import com.toplms.master.repository.TenantUserRepository;
import com.toplms.master.service.EmailAlreadyTakenException;
import com.toplms.master.service.NewUserCommand;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Teachers and Students management. Both are just tenant users filtered by role
 * (INSTRUCTOR / LEARNER), so the two sections share one controller and one pair
 * of templates — the differences (labels, role, URL) are passed in as model
 * attributes.
 */
@Controller
public class AdminMemberController {

    private static final String TEACHER_ROLE = "INSTRUCTOR";
    private static final String STUDENT_ROLE = "LEARNER";

    private final TenantUserService userService;
    private final TenantUserRepository users;
    private final TenantRoleRepository roles;

    public AdminMemberController(TenantUserService userService,
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

    // ---- Teachers ----

    @GetMapping("/app/admin/teachers")
    public String teachers(@AuthenticationPrincipal TenantUserPrincipal p, Model m) {
        return list(p, m, TEACHER_ROLE, "Teachers", "Teacher", "teachers", "/app/admin/teachers");
    }

    @GetMapping("/app/admin/teachers/new")
    public String newTeacher(Model m) {
        return form(m, "Teacher", "teachers", "/app/admin/teachers");
    }

    @PostMapping("/app/admin/teachers")
    public String createTeacher(@Valid @ModelAttribute("memberForm") MemberForm form, BindingResult b,
                                @AuthenticationPrincipal TenantUserPrincipal p, Model m, RedirectAttributes ra) {
        return create(form, b, p, m, ra, TEACHER_ROLE, "Teacher", "teachers", "/app/admin/teachers");
    }

    // ---- Students ----

    @GetMapping("/app/admin/students")
    public String students(@AuthenticationPrincipal TenantUserPrincipal p, Model m) {
        return list(p, m, STUDENT_ROLE, "Students", "Student", "students", "/app/admin/students");
    }

    @GetMapping("/app/admin/students/new")
    public String newStudent(Model m) {
        return form(m, "Student", "students", "/app/admin/students");
    }

    @PostMapping("/app/admin/students")
    public String createStudent(@Valid @ModelAttribute("memberForm") MemberForm form, BindingResult b,
                                @AuthenticationPrincipal TenantUserPrincipal p, Model m, RedirectAttributes ra) {
        return create(form, b, p, m, ra, STUDENT_ROLE, "Student", "students", "/app/admin/students");
    }

    // ---- shared helpers ----

    private String list(TenantUserPrincipal p, Model m, String roleName,
                        String plural, String singular, String active, String baseUrl) {
        m.addAttribute("members", users.findByTenant_IdAndRole_RoleName(p.getTenantId(), roleName));
        m.addAttribute("plural", plural);
        m.addAttribute("singular", singular);
        m.addAttribute("active", active);
        m.addAttribute("baseUrl", baseUrl);
        return "tenant/admin-members";
    }

    private String form(Model m, String singular, String active, String baseUrl) {
        if (!m.containsAttribute("memberForm")) {
            m.addAttribute("memberForm", new MemberForm());
        }
        m.addAttribute("singular", singular);
        m.addAttribute("active", active);
        m.addAttribute("baseUrl", baseUrl);
        return "tenant/admin-member-new";
    }

    private String create(MemberForm form, BindingResult b, TenantUserPrincipal p, Model m,
                          RedirectAttributes ra, String roleName, String singular, String active, String baseUrl) {
        if (b.hasErrors()) {
            return form(m, singular, active, baseUrl);
        }
        TenantRole role = roles.findByTenant_IdAndRoleName(p.getTenantId(), roleName)
                .orElseThrow(() -> new IllegalStateException("Missing role " + roleName + " for tenant"));
        try {
            userService.createUser(new NewUserCommand(
                    p.getTenantId(), form.getFullName(), form.getEmail(), form.getPassword(), role.getId()));
        } catch (EmailAlreadyTakenException e) {
            b.rejectValue("email", "email.taken", "An account with that email already exists");
            return form(m, singular, active, baseUrl);
        }
        ra.addFlashAttribute("created", form.getEmail());
        return "redirect:" + baseUrl;
    }
}
