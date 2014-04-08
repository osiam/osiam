package org.osiam.auth.login;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class LdapAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 5939458742421521211L;

    public LdapAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public LdapAuthenticationToken(Object principal, Object credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
