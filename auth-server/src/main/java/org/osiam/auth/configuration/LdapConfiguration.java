package org.osiam.auth.configuration;

import javax.inject.Inject;

import org.osiam.auth.login.providers.DatabaseAuthoritiesPopulator;
import org.osiam.auth.login.providers.OsiamLdapAuthenticationProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.ldap.authentication.BindAuthenticator;

@Configuration
public class LdapConfiguration {

    @Value("${org.osiam.ldap:false}")
    private boolean isLdapConfigured;
    
    @Value("${org.osiam.ldap.server.groupsearchbase:}")
    private String groupSearchBase;
    
    @Value("#{'${org.osiam.ldap.dn.patterns}'.split(';')}")
    private String[] dnPatterns;
    
    @Value("${org.osiam.ldap.dn.attributes:}")
    private String[] attributes;
    
    @Inject
    private ProviderManager authenticationManager;
    
    @Inject
    private LdapContextSource contextSource;
    
    @Bean
    public OsiamLdapAuthenticationProvider createLdapAuthProvider() {
        if(isLdapConfigured) {
            DatabaseAuthoritiesPopulator rolePopulator = new DatabaseAuthoritiesPopulator(contextSource, groupSearchBase);
            
            BindAuthenticator bindAuthenticator = new BindAuthenticator(contextSource);
            bindAuthenticator.setUserDnPatterns(dnPatterns);
            bindAuthenticator.setUserAttributes(attributes);
            
            OsiamLdapAuthenticationProvider provider = new OsiamLdapAuthenticationProvider(bindAuthenticator, rolePopulator);
            
            authenticationManager.getProviders().add(provider);
        }
        return null;
    }
}
