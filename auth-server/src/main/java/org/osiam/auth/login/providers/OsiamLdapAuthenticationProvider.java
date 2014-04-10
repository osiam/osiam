package org.osiam.auth.login.providers;

import org.osiam.auth.login.authentications.OsiamLdapAuthentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

public class OsiamLdapAuthenticationProvider extends LdapAuthenticationProvider {

    public OsiamLdapAuthenticationProvider(LdapAuthenticator authenticator) {
        super(authenticator);
    }

    public OsiamLdapAuthenticationProvider(LdapAuthenticator authenticator, LdapAuthoritiesPopulator authoritiesPopulator) {
        super(authenticator, authoritiesPopulator);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return super.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (OsiamLdapAuthentication.class.isAssignableFrom(authentication));
    }

}
