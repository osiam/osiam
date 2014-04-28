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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.osiam.auth.exception.LdapConfigurationException;
import org.osiam.auth.login.ldap.OsiamLdapAuthenticationProvider;
import org.osiam.auth.login.ldap.OsiamLdapAuthoritiesPopulator;
import org.osiam.auth.login.ldap.OsiamLdapUserContextMapper;
import org.osiam.resources.scim.User;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;

@Configuration
public class LdapConfiguration implements InitializingBean {

    public static final String LDAP_PROVIDER = "ldap";
    public static final String AUTH_EXTENSION = "urn:org.osiam:scim:extensions:auth-server";

    @Value("${org.osiam.auth-server.ldap.enabled:false}")
    private boolean isLdapConfigured;

    @Value("${org.osiam.auth-server.ldap.server.url:}")
    private String url;

    @Value("${org.osiam.auth-server.ldap.server.groupsearchbase:}")
    private String groupSearchBase;

    @Value("#{'${org.osiam.auth-server.ldap.dn.patterns:}'.split(';')}")
    private String[] dnPatterns;

    private String[] attributes;

    @Value("${org.osiam.auth-server.ldap.mapping:}")
    private String[] attributeMapping;

    private Map<String, String> scimLdapAttributes;

    @Inject
    private ProviderManager authenticationManager;

    @Bean
    public DefaultSpringSecurityContextSource createLdapContextSource() {
        if (isLdapConfigured) {
            return new DefaultSpringSecurityContextSource(url);
        }
        return null;
    }

    private void createLdapToScimAttributeMapping() {
        if (isLdapConfigured && scimLdapAttributes == null) {
            scimLdapAttributes = new HashMap<String, String>();
            for (String keyValuePair : attributeMapping) {
                if (!keyValuePair.contains(":")) {
                    throw new LdapConfigurationException("The ldap attibute mapping value '" + keyValuePair
                            + "' could not be parsed. It doesn't contain a ':'");
                }
                String[] keyValue = keyValuePair.split(":");
                if (keyValue.length != 2) {
                    throw new LdapConfigurationException("The ldap attibute mapping value '" + keyValuePair
                            + "' could not be parsed. It contains more than one ':'");
                }
                scimLdapAttributes.put(keyValue[0].trim(), keyValue[1].trim());
            }

            if (!scimLdapAttributes.containsKey("userName")) {
                scimLdapAttributes.put("userName", "uid");
            }
            attributes = scimLdapAttributes.values().toArray(new String[scimLdapAttributes.size()]);
        }
    }

    @Bean
    public OsiamLdapAuthenticationProvider createLdapAuthProvider() {
        if (isLdapConfigured) {

            createLdapToScimAttributeMapping();

            DefaultSpringSecurityContextSource contextSource = createLdapContextSource();
            OsiamLdapAuthoritiesPopulator rolePopulator = new OsiamLdapAuthoritiesPopulator(contextSource,
                    groupSearchBase);

            BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);
            bindAuthenticator.setUserDnPatterns(dnPatterns);
            bindAuthenticator.setUserAttributes(attributes);

            OsiamLdapUserContextMapper mapper = new OsiamLdapUserContextMapper(scimLdapAttributes);

            OsiamLdapAuthenticationProvider provider = new OsiamLdapAuthenticationProvider(bindAuthenticator,
                    rolePopulator, mapper, scimLdapAttributes);

            authenticationManager.getProviders().add(provider);

            return provider;
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        createLdapToScimAttributeMapping();
        DirContextOperations ldapUserData = new DirContextAdapter();
        for (String scimAttribute : scimLdapAttributes.keySet()) {
            if (scimAttribute.equalsIgnoreCase("password")) {
                throw new LdapConfigurationException(
                        "The password can not be mapped to the SCIM user. Please delete the password mapping from the configuration!");
            }
            ldapUserData.setAttributeValue(scimLdapAttributes.get(scimAttribute), "test@test.de");
        }
        OsiamLdapUserContextMapper contextMapper = new OsiamLdapUserContextMapper(scimLdapAttributes);
        User user = contextMapper.mapUser(ldapUserData);
        contextMapper.mapUpdateUser(user, ldapUserData);
    }
}
