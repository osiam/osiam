package org.osiam.auth.login.internal;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class InternalAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return super.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return InternalAuthentication.class.isAssignableFrom(authentication);
    }
}
