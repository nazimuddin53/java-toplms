package com.toplms.web.tenant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Form for adding a teacher or a student. Same shape for both — the role
 * (INSTRUCTOR / LEARNER) is decided by which section the admin is in, not by the
 * form, so it isn't a field here.
 */
public class MemberForm {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 120, message = "Name must be 2–120 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    private String password;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
