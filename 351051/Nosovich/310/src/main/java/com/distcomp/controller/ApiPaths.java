package com.distcomp.controller;

/**
 * Central place for the versioned API prefix.
 *
 * <p>The prefix is defined once so that a future API version bump (for example
 * {@code /api/v2.0/}) is a single-line change and old clients keep working
 * through a dedicated controller set.</p>
 */
public final class ApiPaths {

    /** Versioned prefix shared by every REST controller. */
    public static final String V1 = "/api/v1.0";

    private ApiPaths() {
    }
}
