package org.osiam.auth.login.ldap

import javax.servlet.http.HttpSession

import org.springframework.ldap.core.DirContextAdapter
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.core.GrantedAuthority

import spock.lang.Specification

class OsiamLdapAuthoritiesPopulatorSpec extends Specification {

    def 'The populator add the user role "ROLE_USER"'() {
        given:
        def role = 'ROLE_USER';
        OsiamLdapAuthoritiesPopulator populator = new OsiamLdapAuthoritiesPopulator(new LdapContextSource(), '')
        DirContextOperations userData = new DirContextAdapter()
        
        when:
        Set<GrantedAuthority> authorities = populator.getAdditionalRoles(userData, '')
        
        then:
        !authorities.empty
        authorities.first().authority == role
    }
}
