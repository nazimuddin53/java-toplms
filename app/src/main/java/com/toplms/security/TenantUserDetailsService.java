package com.toplms.security;

import com.toplms.master.domain.Tenant;
import com.toplms.master.domain.TenantUser;
import com.toplms.master.repository.TenantUserRepository;
import com.toplms.master.repository.TenantRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bridges our database to Spring Security. When a user submits the login form,
 * Spring Security calls {@link #loadUserByUsername} with the typed email, gets
 * back a {@link UserDetails}, then compares the submitted password against
 * {@code UserDetails.getPassword()} using the configured {@code PasswordEncoder}.
 *
 * <p>Because this is the only {@code UserDetailsService} bean in the context,
 * Spring Boot auto-wires it into the authentication machinery — no manual
 * {@code AuthenticationManager} setup required.
 */
@Service
public class TenantUserDetailsService implements UserDetailsService {

    private final TenantUserRepository users;
    private final TenantRepository tenants;

    public TenantUserDetailsService(TenantUserRepository users, TenantRepository tenants) {
        this.users = users;
        this.tenants = tenants;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        TenantUser user = users.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No account for email: " + email));

        // Second explicit lookup to enrich the principal with tenant info.
        Tenant tenant = tenants.findById(user.getTenant().getId())
                .orElseThrow(() -> new UsernameNotFoundException("Tenant missing for user: " + email));

        // Role is a lazy association; safe to touch here because this method is
        // @Transactional, so the Hibernate session is still open.
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : null;

        return new TenantUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFullName(),
                tenant.getId(),
                tenant.getSlug(),
                tenant.getBusinessName(),
                roleName
        );
    }
}
