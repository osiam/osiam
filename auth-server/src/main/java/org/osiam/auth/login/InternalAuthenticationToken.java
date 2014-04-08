package org.osiam.auth.login;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class InternalAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 5020697919339414782L;

    public InternalAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
