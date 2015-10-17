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

import org.osiam.auth.login.OsiamCachingAuthenticationFailureHandler;
import org.osiam.auth.login.internal.InternalAuthenticationProvider;
import org.osiam.auth.login.ldap.OsiamLdapAuthenticationProvider;
import org.osiam.security.helper.LoginDecisionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Order
@EnableWebSecurity
public class WebApplicationSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private InternalAuthenticationProvider internalAuthenticationProvider;

    @Autowired(required = false)
    private OsiamLdapAuthenticationProvider osiamLdapAuthenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/login")
                .permitAll()
                .antMatchers("/oauth/**")
                .authenticated()
                .and()
                // TODO: This is a bad idea! We need CSRF at least for the `/oauth/authorize` endpoint
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(loginUrlAuthenticationEntryPoint())
                .accessDeniedPage("/login/error")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                .addFilterBefore(loginDecisionFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(internalAuthenticationProvider);
        if (osiamLdapAuthenticationProvider != null) {
            auth.authenticationProvider(osiamLdapAuthenticationProvider);
        }
    }

    @Override
    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public ShaPasswordEncoder passwordEncoder() {
        ShaPasswordEncoder passwordEncoder = new ShaPasswordEncoder(512);
        passwordEncoder.setIterations(1000);
        return passwordEncoder;
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler successHandler() {
        SavedRequestAwareAuthenticationSuccessHandler successHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setAlwaysUseDefaultTargetUrl(false);
        return successHandler;
    }

    @Bean
    public OsiamCachingAuthenticationFailureHandler failureHandler() throws Exception {
        return new OsiamCachingAuthenticationFailureHandler("/login/error");
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint("/login");
    }

    @Bean
    public LoginDecisionFilter loginDecisionFilter() throws Exception {
        LoginDecisionFilter loginDecisionFilter = new LoginDecisionFilter();
        loginDecisionFilter.setAuthenticationManager(authenticationManagerBean());
        loginDecisionFilter.setAuthenticationSuccessHandler(successHandler());
        loginDecisionFilter.setAuthenticationFailureHandler(failureHandler());
        return loginDecisionFilter;
    }
}
