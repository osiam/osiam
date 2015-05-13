package org.osiam.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * This class is responsible for invalidating the session after the user got the grant.
 * We need this, because when a user is deactivated, his access token will be revoked too.
 * After the token revocation the user should not be able to login without the login form.
 * If the session will not be invalidated, the user will get a new access token without
 * need for re-login.
 *
 * This class is copied from
 * @see https://github.com/spring-projects/spring-security-oauth/issues/140
 */
@Service
@Aspect
public class SessionInvalidationAspect {

    private static final String FORWARD_OAUTH_CONFIRM_ACCESS = "forward:/oauth/confirm_access";
    private static final Logger logger = Logger.getLogger(SessionInvalidationAspect.class);

    @AfterReturning(value = "within(org.springframework.security.oauth2.provider.endpoint..*) && @annotation(org.springframework.web.bind.annotation.RequestMapping)", returning = "result")
    public void authorizationAdvice(JoinPoint joinpoint, ModelAndView result) throws Throwable {

        // If we're not going to the confirm_access page, it means approval has been skipped due to existing access
        // token or something else and they'll be being sent back to app. Time to end session.
        if (!FORWARD_OAUTH_CONFIRM_ACCESS.equals(result.getViewName())) {
            invalidateSession();
        }
    }

    @AfterReturning(value = "within(org.springframework.security.oauth2.provider.endpoint..*) && @annotation(org.springframework.web.bind.annotation.RequestMapping)", returning = "result")
    public void authorizationAdvice(JoinPoint joinpoint, View result) throws Throwable {
        // Anything returning a view and not a ModelView is going to be redirecting outside of the app (I think).
        // This happens after the authorize approve / deny page with the POST to /oauth/authorize. This is the time
        // to kill the session since they'll be being sent back to the requesting app.
        invalidateSession();
    }

    @AfterThrowing(value = "within(org.springframework.security.oauth2.provider.endpoint..*) &&  @annotation(org.springframework.web.bind.annotation.RequestMapping)", throwing = "error")
    public void authorizationErrorAdvice(JoinPoint joinpoint) throws Throwable {
        invalidateSession();
    }

    private void invalidateSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession(false);

        if (session != null) {
            logger.warn(String.format("As part of OAuth application grant processing, invalidating session for request %s", request.getRequestURI()));

            session.invalidate();
            SecurityContextHolder.clearContext();
        }
    }

}