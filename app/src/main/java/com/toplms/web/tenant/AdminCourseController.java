package com.toplms.web.tenant;

import com.toplms.security.TenantUserPrincipal;
import com.toplms.tenant.service.CourseService;
import com.toplms.tenant.service.NewCourseCommand;
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

/** Course management under {@code /app/admin/courses} (ROLE_ADMIN). */
@Controller
@RequestMapping("/app/admin/courses")
public class AdminCourseController {

    private final CourseService courseService;

    public AdminCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @ModelAttribute
    public void common(@AuthenticationPrincipal TenantUserPrincipal principal, Model model) {
        if (principal != null) {
            model.addAttribute("tenantName", principal.getTenantName());
        }
    }

    @GetMapping
    public String list(@AuthenticationPrincipal TenantUserPrincipal principal, Model model) {
        model.addAttribute("courses", courseService.listForTenant(principal.getTenantId()));
        return "tenant/admin-courses";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        if (!model.containsAttribute("courseForm")) {
            model.addAttribute("courseForm", new CourseForm());
        }
        return "tenant/admin-course-new";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("courseForm") CourseForm form,
                         BindingResult binding,
                         @AuthenticationPrincipal TenantUserPrincipal principal,
                         RedirectAttributes ra) {
        if (binding.hasErrors()) {
            return "tenant/admin-course-new";
        }
        courseService.create(new NewCourseCommand(
                principal.getTenantId(), form.getTitle(), form.getDescription()));
        ra.addFlashAttribute("created", form.getTitle());
        return "redirect:/app/admin/courses";
    }
}
