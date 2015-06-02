package org.osiam.security.helper;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides helper methods for access token handling.
 */
public class AccessTokenHelper {

    private AccessTokenHelper() {
    }

    /**
     * Returns the bearer token from the authorization header of the given {@link HttpServletRequest}.
     * 
     * @param request
     *            request object of which to extract the bearer token of the authorization header
     * @return the bearer token
     * @throws IllegalArgumentException
     *             if no authorization header was found
     */
    public static String getBearerToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            throw new IllegalArgumentException("No access token submitted!");
        }
        return authorization.substring("Bearer ".length(), authorization.length());
    }
}
