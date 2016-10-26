/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.configuration;

import org.osiam.security.authorization.OsiamMethodSecurityExpressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private DefaultTokenServices tokenService;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId("oauth2res")
                .tokenServices(tokenService)
                .expressionHandler(new OsiamMethodSecurityExpressionHandler());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.authorizeRequests()
                .antMatchers("/ServiceProviderConfig").permitAll()
                .antMatchers("/management/**").access("#oauth2.hasScope('ADMIN')")
                .antMatchers("/Me/**").access("#oauth2.hasScope('ADMIN') or #oauth2.hasScope('ME')")
                .antMatchers(HttpMethod.POST, "/Users/**").access("#oauth2.hasScope('ADMIN')")
                .regexMatchers(HttpMethod.GET, "/Users/?").access("#oauth2.hasScope('ADMIN')")
                .antMatchers("/Users/**")
                    .access("#oauth2.hasScope('ADMIN') or #oauth2.hasScope('ME') and #osiam.isOwnerOfResource()")
                .antMatchers("/token/validation").authenticated()
                .antMatchers("/token/revocation", "/token/revocation/")
                    .access("#oauth2.hasScope('ADMIN') or #oauth2.hasScope('ME')")
                .antMatchers("/token/revocation/**")
                    .access("#oauth2.hasScope('ADMIN') or #oauth2.hasScope('ME') and #osiam.isOwnerOfResource()")
                .anyRequest().access("#oauth2.hasScope('ADMIN')");
        // @formatter:on
    }
}
