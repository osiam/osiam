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

import com.google.common.collect.Iterables;
import org.osiam.auth.login.ldap.OsiamLdapAuthenticationProvider;
import org.osiam.auth.login.ldap.OsiamLdapUserContextMapper;
import org.osiam.auth.login.ldap.ScimToLdapAttributeMapping;
import org.osiam.resources.provisioning.SCIMUserProvisioning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty("org.osiam.ldap.enabled")
@ConfigurationProperties("org.osiam.ldap")
public class LdapAuthentication {

    public static final String LDAP_PROVIDER = "ldap";
    public static final String AUTH_EXTENSION = "urn:org.osiam:scim:extensions:auth-server";

    private String serverUrl = "";
    private String[] dnPatterns = new String[0];
    private Map<String, String> userMapping = new HashMap<>();

    @Bean
    public ScimToLdapAttributeMapping ldapToScimAttributeMapping() {
        return new ScimToLdapAttributeMapping(userMapping);
    }

    @Bean
    @Autowired
    public OsiamLdapAuthenticationProvider osiamLdapAuthenticationProvider(SCIMUserProvisioning userProvisioning,
                                                                           @Value("${org.osiam.ldap.sync-user-data:true}") boolean syncUserData) {
        return new OsiamLdapAuthenticationProvider(
                bindAuthenticator(),
                new DefaultLdapAuthoritiesPopulator(contextSource(), ""),
                new OsiamLdapUserContextMapper(ldapToScimAttributeMapping()),
                userProvisioning,
                syncUserData);
    }

    @Bean
    public BaseLdapPathContextSource contextSource() {
        return new DefaultSpringSecurityContextSource(serverUrl);
    }

    @Bean
    public LdapAuthenticator bindAuthenticator() {
        BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource());
        bindAuthenticator.setUserDnPatterns(dnPatterns);
        bindAuthenticator.setUserAttributes(
                Iterables.toArray(ldapToScimAttributeMapping().ldapAttributes(), String.class)
        );
        return bindAuthenticator;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String[] getDnPatterns() {
        return dnPatterns;
    }

    public Map<String, String> getUserMapping() {
        return userMapping;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setDnPatterns(String[] dnPatterns) {
        this.dnPatterns = dnPatterns;
    }
}
