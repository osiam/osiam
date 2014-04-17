package org.osiam.auth.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.naming.ConfigurationException;

import org.osiam.auth.login.ldap.OsiamLdapAuthenticationProvider;
import org.osiam.auth.login.ldap.OsiamLdapAuthoritiesPopulator;
import org.osiam.auth.login.ldap.OsiamLdapUserContextMapper;
import org.osiam.auth.login.ldap.OsiamLdapUserSynchronizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;

@Configuration
public class LdapConfiguration {

    @Value("${org.osiam.auth.ldap.enabled:false}")
    private boolean isLdapConfigured;

    @Value("${org.osiam.auth.ldap.server.url}")
    private String url;

    @Value("${org.osiam.auth.ldap.server.groupsearchbase:}")
    private String groupSearchBase;

    @Value("#{'${org.osiam.auth.ldap.dn.patterns}'.split(';')}")
    private String[] dnPatterns;

    private String[] attributes;

    @Value("${org.osiam.auth.ldap.mapping:}")
    private String[] attributeMapping;

    @Inject
    private ProviderManager authenticationManager;

    @Bean
    public DefaultSpringSecurityContextSource createLdapContextSource() {
        if (isLdapConfigured) {
            return new DefaultSpringSecurityContextSource(url);
        }
        return null;
    }

    @Bean
    public OsiamLdapUserSynchronizer createSynchroniser() {
        if (isLdapConfigured) {
            Map<String, String> scimLdapAttributes = new HashMap<String, String>();
            for (String keyValuePair : attributeMapping) {
                if (!keyValuePair.contains(":")) {
                    new ConfigurationException("The ldap attibute mapping value '" + keyValuePair
                            + "' could not be parsed. It doesn't contain a ':'");
                }
                String[] keyValue = keyValuePair.split(":");
                if(keyValue.length != 2){
                    new ConfigurationException("The ldap attibute mapping value '" + keyValuePair
                            + "' could not be parsed. It contains more than one ':'");
                }
                scimLdapAttributes.put(keyValue[0].trim(), keyValue[1].trim());
            }

            if(!scimLdapAttributes.containsKey("userName")){
                scimLdapAttributes.put("userName", "uid");
            }
            attributes = scimLdapAttributes.values().toArray(new String[scimLdapAttributes.size()]);
            
            return new OsiamLdapUserSynchronizer(scimLdapAttributes);
        }
        return null;
    }

    @Bean
    public OsiamLdapAuthenticationProvider createLdapAuthProvider() {
        if (isLdapConfigured) {

            DefaultSpringSecurityContextSource contextSource = createLdapContextSource();
            OsiamLdapAuthoritiesPopulator rolePopulator = new OsiamLdapAuthoritiesPopulator(contextSource,
                    groupSearchBase);

            BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);
            bindAuthenticator.setUserDnPatterns(dnPatterns);
            bindAuthenticator.setUserAttributes(attributes);

            OsiamLdapAuthenticationProvider provider = new OsiamLdapAuthenticationProvider(bindAuthenticator,
                    rolePopulator);
            provider.setUserDetailsContextMapper(new OsiamLdapUserContextMapper());

            authenticationManager.getProviders().add(provider);

            return provider;
        }
        return null;
    }
}
