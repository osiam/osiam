package org.osiam.auth.login.internal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class InternalAuthentication extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 5020697919339414782L;

    public InternalAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
