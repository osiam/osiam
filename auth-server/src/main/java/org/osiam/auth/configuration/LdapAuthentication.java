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

import com.google.common.collect.Iterables;
import org.osiam.auth.login.ldap.OsiamLdapAuthenticationProvider;
import org.osiam.auth.login.ldap.OsiamLdapUserContextMapper;
import org.osiam.auth.login.ldap.ScimToLdapAttributeMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

@Configuration
@ConditionalOnProperty(prefix = "org.osiam.auth-server", name = "ldap.enabled")
public class LdapAuthentication {

    public static final String LDAP_PROVIDER = "ldap";
    public static final String AUTH_EXTENSION = "urn:org.osiam:scim:extensions:auth-server";

    @Value("${org.osiam.auth-server.ldap.server.url:}")
    private String url;

    @Value("${org.osiam.auth-server.ldap.server.groupsearchbase:}")
    private String groupSearchBase;

    @Value("#{'${org.osiam.auth-server.ldap.dn.patterns:}'.split(';')}")
    private String[] dnPatterns;

    @Value("${org.osiam.auth-server.ldap.mapping:}")
    private String[] attributeMapping;

    @Bean
    public ScimToLdapAttributeMapping ldapToScimAttributeMapping() {
        return new ScimToLdapAttributeMapping(attributeMapping);
    }

    @Bean
    public OsiamLdapAuthenticationProvider osiamLdapAuthenticationProvider() {
        return new OsiamLdapAuthenticationProvider(
                bindAuthenticator(),
                new DefaultLdapAuthoritiesPopulator(contextSource(), groupSearchBase),
                new OsiamLdapUserContextMapper(ldapToScimAttributeMapping()));
    }

    @Bean
    public BaseLdapPathContextSource contextSource() {
        return new DefaultSpringSecurityContextSource(url);
    }

    @Bean
    public LdapAuthenticator bindAuthenticator(){
        BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource());
        bindAuthenticator.setUserDnPatterns(dnPatterns);
        bindAuthenticator.setUserAttributes(
                Iterables.toArray(ldapToScimAttributeMapping().ldapAttributes(), String.class)
        );
        return bindAuthenticator;
    }
}
