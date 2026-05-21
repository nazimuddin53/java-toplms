package com.toplms.web.publicpage;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Year;

/**
 * Serves the public welcome page at {@code GET /}.
 *
 * <p>{@code @Controller} (not {@code @RestController}): a {@code @Controller}
 * method returns a <em>view name</em> — here the string {@code "public/index"} —
 * which Spring + Thymeleaf resolve to
 * {@code src/main/resources/templates/public/index.html}. A {@code @RestController}
 * would instead write the return value straight to the HTTP response body as JSON.
 *
 * <p>This class lives under {@code com.toplms.web.publicpage}, which is below
 * the package of {@link com.toplms.ToplmsApplication} — so the component scan
 * implied by {@code @SpringBootApplication} picks it up automatically.
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
        model.addAttribute("currentYear", Year.now().getValue());
        return "public/index";
    }
}
