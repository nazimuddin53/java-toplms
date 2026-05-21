package com.toplms.web.auth;

import com.toplms.master.service.EmailAlreadyTakenException;
import com.toplms.master.service.NewTenantCommand;
import com.toplms.master.service.SlugAlreadyTakenException;
import com.toplms.master.service.TenantProvisioningService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Public signup flow. The controller's job is the web boundary only: render the
 * form, validate input format, translate the form into a domain command, hand
 * off to the service, and turn the outcome into a redirect.
 *
 * <p>All the business logic (uniqueness checks, password hashing, the two
 * inserts in one transaction) lives in {@link TenantProvisioningService} in
 * {@code engine/} — the controller never touches the database.
 */
@Controller
public class SignupController {

    private final TenantProvisioningService provisioningService;

    public SignupController(TenantProvisioningService provisioningService) {
        this.provisioningService = provisioningService;
    }

    @GetMapping("/signup")
    public String show(Model model) {
        if (!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "public/signup";
    }

    /**
     * Handles {@code POST /signup}.
     *
     * <p>Two layers of validation: {@code @Valid} runs the form's format rules
     * ({@code @Email}, {@code @Pattern}, …) into {@link BindingResult}; the
     * service then enforces business rules (slug/email uniqueness) the form
     * can't know about. A business-rule failure comes back as an exception,
     * which we convert into a field error so it renders next to the right input.
     */
    @PostMapping("/signup")
    public String submit(@Valid @ModelAttribute("signupForm") SignupForm form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "public/signup";
        }

        try {
            provisioningService.provision(new NewTenantCommand(
                    form.getBusinessName(),
                    form.getSlug(),
                    form.getFullName(),
                    form.getEmail(),
                    form.getPassword()
            ));
        } catch (SlugAlreadyTakenException e) {
            bindingResult.rejectValue("slug", "slug.taken", "That workspace URL is already taken");
            return "public/signup";
        } catch (EmailAlreadyTakenException e) {
            bindingResult.rejectValue("email", "email.taken", "An account with that email already exists");
            return "public/signup";
        }

        // POST/Redirect/GET → land on the login page with a success banner.
        return "redirect:/login?registered";
    }
}
