package com.toplms.master.service;

/** Thrown by {@link TenantProvisioningService} when an email is already registered. */
public class EmailAlreadyTakenException extends RuntimeException {

    public EmailAlreadyTakenException(String email) {
        super("Email already registered: " + email);
    }
}
