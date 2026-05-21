package com.toplms.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Decides where a user lands right after a successful login, based on their role.
 *
 * <p>Spring Security calls {@code onAuthenticationSuccess} once credentials check
 * out. Wiring this in via {@code formLogin().successHandler(...)} replaces the
 * fixed {@code defaultSuccessUrl} — letting admins go to the admin dashboard
 * while everyone else goes to their profile.
 */
@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        String target = isAdmin ? "/app/admin" : "/app/profile";
        response.sendRedirect(request.getContextPath() + target);
    }
}
