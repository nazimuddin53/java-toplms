package com.toplms.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the public welcome page.
 *
 * <p>{@code @Controller} (not {@code @RestController}): a {@code @Controller}
 * method returns a <em>view name</em> — here the String {@code "index"} — which
 * Spring + Thymeleaf resolve to {@code src/main/resources/templates/index.html}.
 * A {@code @RestController} would instead write the return value straight to the
 * HTTP response body as JSON/text.
 *
 * <p>This class lives under {@code com.toplms} (the package of
 * {@code ToplmsApplication}), so {@code @SpringBootApplication}'s component scan
 * picks it up automatically — no manual registration needed.
 */
@Controller
public class HomeController {

    /**
     * Handles {@code GET /}. The {@link Model} is a map of attributes passed to
     * the template; in {@code index.html} they are read with Thymeleaf
     * expressions like {@code th:text="${appName}"}.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appName", "toplms");
        model.addAttribute("tagline", "Multi-tenant Learning Management System");
        return "index";
    }
}
