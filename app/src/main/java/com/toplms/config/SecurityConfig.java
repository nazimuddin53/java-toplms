package com.toplms.config;

import com.toplms.security.RoleBasedAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Central Spring Security configuration.
 *
 * <p>Once {@code spring-boot-starter-security} is on the classpath, EVERY request
 * is denied unless we say otherwise. The {@link SecurityFilterChain} bean below
 * defines the rules; Spring Security builds its servlet filter chain from it.
 *
 * <p>We don't define an {@code AuthenticationManager} or
 * {@code AuthenticationProvider} by hand: Spring Boot auto-wires one from the
 * single {@code UserDetailsService} bean ({@code TenantUserDetailsService}) plus
 * the {@link PasswordEncoder} bean below. On login it loads the user by email,
 * then uses the encoder to check the submitted password against the stored hash.
 */
@Configuration
public class SecurityConfig {

    private final RoleBasedAuthenticationSuccessHandler successHandler;

    public SecurityConfig(RoleBasedAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Authorization rules, top-to-bottom. More specific rules first.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/signup", "/signup/**", "/login", "/error",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        // Admin dashboard: only users whose role is ADMIN
                        // (authority ROLE_ADMIN) may enter.
                        .requestMatchers("/app/admin", "/app/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()       // everything else needs login
                )
                // Form-based login. Spring Security handles POST /login itself
                // (checking credentials) — we only supply the GET /login page.
                // successHandler routes by role instead of a fixed landing page.
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                // POST /logout (CSRF-protected) clears the session.
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")
                        .permitAll()
                );
        // CSRF protection is left ON (the default). Our Thymeleaf forms include
        // the token via a hidden field, so POSTs from our own pages succeed while
        // forged cross-site POSTs are rejected.
        return http.build();
    }

    /**
     * BCrypt hashing for passwords. Used in two places: the provisioning service
     * hashes new passwords with it, and the login flow verifies submitted
     * passwords against the stored hash with it. One bean, shared by both.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
