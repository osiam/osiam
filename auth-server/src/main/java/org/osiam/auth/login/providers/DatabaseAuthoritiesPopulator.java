package org.osiam.auth.login.providers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;

public class DatabaseAuthoritiesPopulator extends DefaultLdapAuthoritiesPopulator{

    public DatabaseAuthoritiesPopulator(ContextSource contextSource, String groupSearchBase) {
        super(contextSource, groupSearchBase);
    }
    
    public Set<GrantedAuthority> getAdditionalRoles(DirContextOperations user, String username) { 
        //Get roles from a datasource of your choice
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add((new SimpleGrantedAuthority("ROLE_USER")));
        return authorities;
    }
}
