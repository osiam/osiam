package org.osiam.auth.login.ldap;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class OsiamLdapAuthentication extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 5939458742421521211L;

    public OsiamLdapAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }
}
