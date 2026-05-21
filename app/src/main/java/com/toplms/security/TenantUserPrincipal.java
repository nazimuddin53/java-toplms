package com.toplms.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * The authenticated principal — Spring Security's view of "who is logged in".
 *
 * <p>{@link UserDetails} is the contract Spring Security needs: a username, a
 * (hashed) password to compare against, and authorities (roles). We extend it
 * with tenant info ({@code tenantId}, {@code tenantSlug}, {@code tenantName}) so
 * controllers can render the right workspace without another DB hit.
 *
 * <p>Once a user logs in, an instance of this lives in the security context for
 * the session; controllers grab it with {@code @AuthenticationPrincipal}.
 */
public class TenantUserPrincipal implements UserDetails {

    private final Long userId;
    private final String email;
    private final String passwordHash;
    private final String fullName;
    private final Long tenantId;
    private final String tenantSlug;
    private final String tenantName;
    private final String roleName;

    public TenantUserPrincipal(Long userId, String email, String passwordHash, String fullName,
                               Long tenantId, String tenantSlug, String tenantName, String roleName) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.tenantId = tenantId;
        this.tenantSlug = tenantSlug;
        this.tenantName = tenantName;
        this.roleName = roleName;
    }

    // --- UserDetails contract ---

    /**
     * Authorities are derived from the user's role name. Spring Security's
     * convention is that role authorities are prefixed {@code ROLE_}, so role
     * {@code "ADMIN"} becomes authority {@code "ROLE_ADMIN"} — which is exactly
     * what {@code hasRole("ADMIN")} checks for. A user with no role gets no
     * authorities (authenticated, but can't reach role-gated pages).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roleName == null || roleName.isBlank()) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    /** The hash Spring Security compares the submitted password against. */
    @Override
    public String getPassword() {
        return passwordHash;
    }

    /** Spring Security's "username" is our email. */
    @Override
    public String getUsername() {
        return email;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // --- tenant extras ---

    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public Long getTenantId() { return tenantId; }
    public String getTenantSlug() { return tenantSlug; }
    public String getTenantName() { return tenantName; }
    public String getRoleName() { return roleName; }
}
