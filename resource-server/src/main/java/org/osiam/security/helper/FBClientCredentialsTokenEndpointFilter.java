package org.osiam.security.helper;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.BadClientCredentialsException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter is basically just a fork of ClientCredentialsTokenEndpointFilter apparently they have only one constructor
 * which shows to the wrong url in our case (oauth/token) we need two endpoint:
 * the first on is for oauth2 in general: oauth/token
 * the second one if for facebook: /fb/oauth/access_token
 * Since facebook does not enforce http basic we don't do it either, therefore we need a filter
 * to authenticate a client via send parameter (clientid, client_secret).
 */
public class FBClientCredentialsTokenEndpointFilter extends AbstractAuthenticationProcessingFilter {

    private AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();


    protected FBClientCredentialsTokenEndpointFilter() {
        super("/fb/oauth/access_token");
    }

    @Override
    /**
     * Sets the handler, failed -> BadCredentialsException, success -> just continue.
     */
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException exception)
                    throws IOException, ServletException {
                if (exception instanceof BadCredentialsException) {
                    exception = // NOSONAR
                            new BadCredentialsException(exception.getMessage(), new BadClientCredentialsException());
                }
                authenticationEntryPoint.commence(request, response, exception);
            }
        });
        setAuthenticationSuccessHandler(new MyAuthenticationSuccessHandler());
    }

    private static class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            // no-op - just allow filter chain to continue to token endpoint
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        if (clientId == null) {
            return null;
        }
        if (clientSecret == null) {
            clientSecret = "";
        }
        clientId = clientId.trim();
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(clientId, clientSecret);
        return this.getAuthenticationManager().authenticate(authRequest);

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String clientId = request.getParameter("client_id");
        if (clientId == null) {
            // Give basic auth a chance to work instead (it's preferred anyway)
            return false;
        }

        return super.requiresAuthentication(request, response);
    }
}

