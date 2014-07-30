package org.osiam.security.helper;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides helper methods for access token handling.
 */
public class AccessTokenHelper {

    /**
     * Returns the bearer token from the authorization header of the given {@link HttpServletRequest}.
     * 
     * @param request
     *        the request
     * @return the bearer token
     * @throws IllegalArgumentException
     *         if no authorization header was fouund
     */
    public String getBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            throw new IllegalArgumentException("No access_token submitted!");
        }
        return authorization.substring("Bearer ".length(), authorization.length());
    }
}
