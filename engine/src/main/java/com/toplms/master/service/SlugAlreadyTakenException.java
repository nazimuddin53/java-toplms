package com.toplms.master.service;

/** Thrown by {@link TenantProvisioningService} when a workspace slug is in use. */
public class SlugAlreadyTakenException extends RuntimeException {

    public SlugAlreadyTakenException(String slug) {
        super("Workspace URL already taken: " + slug);
    }
}
