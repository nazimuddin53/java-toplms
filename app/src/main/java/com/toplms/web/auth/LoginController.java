package com.toplms.web.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the login page at {@code GET /login}.
 *
 * <p>This is the ONLY part of login we write. The actual credential check
 * happens at {@code POST /login}, which Spring Security's filter intercepts and
 * handles — we never write that handler. SecurityConfig points form login at
 * this page via {@code .loginPage("/login")}.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
