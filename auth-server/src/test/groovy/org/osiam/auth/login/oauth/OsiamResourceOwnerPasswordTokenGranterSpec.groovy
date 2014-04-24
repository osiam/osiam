/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.auth.login.oauth

import org.osiam.auth.login.internal.InternalAuthentication
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication

import spock.lang.Specification

class OsiamResourceOwnerPasswordTokenGranterSpec extends Specification {

    def 'the osiam granter uses the InternalAuthentication token for the client authentication'() {
        given:
        ProviderManager authenticationManager = Mock()
        OsiamResourceOwnerPasswordTokenGranter osiamGranter = new OsiamResourceOwnerPasswordTokenGranter(authenticationManager, null, null)
        AuthorizationRequest request = new DefaultAuthorizationRequest(new HashMap())
        InternalAuthentication authentication = new InternalAuthentication('username', 'password', new ArrayList<GrantedAuthority>())
        
        when:
        OAuth2Authentication auth = osiamGranter.getOAuth2Authentication(request)
        
        then:
        authenticationManager.authenticate(_) >> authentication
        auth.getUserAuthentication() instanceof InternalAuthentication
    }
}
