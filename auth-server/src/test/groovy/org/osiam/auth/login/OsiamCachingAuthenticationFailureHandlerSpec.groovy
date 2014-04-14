package org.osiam.auth.login

import org.osiam.security.helper.LoginDecisionFilter;

class OsiamCachingAuthenticationFailureHandlerSpec {

    LoginDecisionFilter filter = new LoginDecisionFilter()
    OsiamCachingAuthenticationFailureHandler failureHandler = new OsiamCachingAuthenticationFailureHandler(loginDecisionFilter: filter)
    
}
