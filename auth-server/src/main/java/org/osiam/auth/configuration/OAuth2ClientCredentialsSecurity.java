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

package org.osiam.auth.configuration;

import org.osiam.security.authentication.OsiamClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
public class OAuth2ClientCredentialsSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private OsiamClientDetailsService osiamClientDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(new ClientDetailsUserDetailsService(osiamClientDetailsService));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
                .antMatchers("/oauth/token", "/fb/oauth/access_token")
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .addFilterAfter(clientCredentialsTokenEndpointFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(fbClientCredentialsTokenEndpointFilter(), BasicAuthenticationFilter.class)
                .httpBasic()
                .authenticationEntryPoint(clientAuthenticationEntryPoint())
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }

    public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter() throws Exception {
        ClientCredentialsTokenEndpointFilter tokenEndpointFilter = new ClientCredentialsTokenEndpointFilter();
        tokenEndpointFilter.setAuthenticationManager(authenticationManager());
        return tokenEndpointFilter;
    }

    public ClientCredentialsTokenEndpointFilter fbClientCredentialsTokenEndpointFilter() throws Exception {
        ClientCredentialsTokenEndpointFilter tokenEndpointFilter =
                new ClientCredentialsTokenEndpointFilter("/fb/oauth/access_token");
        tokenEndpointFilter.setAuthenticationManager(authenticationManager());
        tokenEndpointFilter.afterPropertiesSet();
        return tokenEndpointFilter;
    }

    public OAuth2AuthenticationEntryPoint clientAuthenticationEntryPoint() {
        OAuth2AuthenticationEntryPoint authenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        authenticationEntryPoint.setRealmName("authorization-server/client");
        return authenticationEntryPoint;
    }
}
