package org.osiam.auth.configuration;

import javax.inject.Inject;

import org.osiam.auth.login.ldap.OsiamLdapAuthenticationProvider;
import org.osiam.auth.login.ldap.OsiamLdapAuthoritiesPopulator;
import org.osiam.auth.login.ldap.OsiamLdapUserContextMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;

@Configuration
public class LdapConfiguration {

    @Value("${org.osiam.ldap.enabled:false}")
    private boolean isLdapConfigured;

    @Value("${org.osiam.ldap.server.url}")
    private String url;

    @Value("${org.osiam.ldap.server.groupsearchbase:}")
    private String groupSearchBase;

    @Value("#{'${org.osiam.ldap.dn.patterns}'.split(';')}")
    private String[] dnPatterns;

    @Value("${org.osiam.ldap.dn.attributes:}")
    private String[] attributes;

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
