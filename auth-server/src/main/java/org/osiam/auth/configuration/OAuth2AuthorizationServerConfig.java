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

import org.osiam.auth.login.oauth.OsiamResourceOwnerPasswordTokenGranter;
import org.osiam.auth.token.OsiamTokenEnhancer;
import org.osiam.security.authentication.OsiamClientDetailsService;
import org.osiam.security.authorization.OsiamUserApprovalHandler;
import org.osiam.security.helper.LessStrictRedirectUriAuthorizationCodeTokenGranter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OsiamClientDetailsService osiamClientDetailsService;

    @Autowired
    private OsiamUserApprovalHandler userApprovalHandler;

    @Value("${org.osiam.auth-server.home}")
    private String authServerHome;

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.userApprovalHandler(userApprovalHandler)
                .requestFactory(oAuth2RequestFactory())
                .authorizationCodeServices(authorizationCodeServices())
                .tokenServices(tokenServices())
                .tokenEnhancer(osiamTokenEnhancer())
                .tokenGranter(tokenGranter());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(osiamClientDetailsService).build();
    }

    @Bean
    public TokenGranter tokenGranter() throws Exception {
        return new CompositeTokenGranter(Arrays.asList(new TokenGranter[]{
                new ClientCredentialsTokenGranter(
                        tokenServices(), osiamClientDetailsService, oAuth2RequestFactory()
                ),
                new OsiamResourceOwnerPasswordTokenGranter(
                        authenticationManager, tokenServices(), osiamClientDetailsService, oAuth2RequestFactory()
                ),
                new RefreshTokenGranter(
                        tokenServices(), osiamClientDetailsService, oAuth2RequestFactory()
                ),
                new LessStrictRedirectUriAuthorizationCodeTokenGranter(
                        tokenServices(), authorizationCodeServices(), osiamClientDetailsService, oAuth2RequestFactory()
                )
        }));
    }

    @Bean
    public OAuth2RequestFactory oAuth2RequestFactory() {
        return new DefaultOAuth2RequestFactory(osiamClientDetailsService);
    }

    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new InMemoryAuthorizationCodeServices();
    }

    @Bean
    public DefaultTokenServices tokenServices() throws Exception {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setReuseRefreshToken(true);
        tokenServices.setClientDetailsService(osiamClientDetailsService);
        tokenServices.setTokenEnhancer(osiamTokenEnhancer());
        tokenServices.afterPropertiesSet();
        return tokenServices;
    }

    @Bean
    public TokenEnhancer osiamTokenEnhancer() {
        return new OsiamTokenEnhancer();
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }
}
