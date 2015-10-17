package org.osiam.util

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class TestAuthentication implements Authentication {

    private boolean authenticated = false

    TestAuthentication(boolean authenticated) {
        this.authenticated = authenticated
    }

    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return null
    }

    @Override
    Object getCredentials() {
        return null
    }

    @Override
    Object getDetails() {
        return null
    }

    @Override
    Object getPrincipal() {
        return null
    }

    @Override
    boolean isAuthenticated() {
        return authenticated
    }

    @Override
    void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated
    }

    @Override
    String getName() {
        return null
    }
}
