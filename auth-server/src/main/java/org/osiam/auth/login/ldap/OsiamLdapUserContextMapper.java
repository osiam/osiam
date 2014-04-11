package org.osiam.auth.login.ldap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.ppolicy.PasswordPolicyControl;
import org.springframework.security.ldap.ppolicy.PasswordPolicyResponseControl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

public class OsiamLdapUserContextMapper extends LdapUserDetailsMapper implements AttributesMapper {
    
    private final Log logger = LogFactory.getLog(LdapUserDetailsMapper.class);
    private String passwordAttributeName = "userPassword";
    private String[] roleAttributes = null;

    @Override
    public Map<String, String> mapFromAttributes(Attributes attributes) throws NamingException {
        Map<String, String> map = new HashMap<String, String>();

        Attribute attr = attributes.get("displayName");
        String fullname = attr != null ? (String) attr.get() : "";
        attr = attributes.get("mail");
        String email = attr != null ? (String) attr.get() : "";
        attr = attributes.get("title");
        String title = attr != null ? (String) attr.get() : "";

        map.put("fullname", fullname);
        map.put("email", email);
        map.put("title", title);
        return map;
    }

    @Override
    public LdapUserDetailsImpl mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        String dn = ctx.getNameInNamespace();

        logger.debug("Mapping user details from context with DN: " + dn);

        LdapUserDetailsImpl.Essence essence = new LdapUserDetailsImpl.Essence();
        essence.setDn(dn);

        Object passwordValue = ctx.getObjectAttribute(passwordAttributeName);

        if (passwordValue != null) {
            essence.setPassword(mapPassword(passwordValue));
        }

        essence.setUsername(username);

        // Map the roles
        for (int i = 0; (roleAttributes != null) && (i < roleAttributes.length); i++) {
            String[] rolesForAttribute = ctx.getStringAttributes(roleAttributes[i]);

            if (rolesForAttribute == null) {
                logger.debug("Couldn't read role attribute '" + roleAttributes[i] + "' for user " + dn);
                continue;
            }

            for (String role : rolesForAttribute) {
                GrantedAuthority authority = createAuthority(role);

                if (authority != null) {
                    essence.addAuthority(authority);
                }
            }
        }

        // Add the supplied authorities

        for (GrantedAuthority authority : authorities) {
            essence.addAuthority(authority);
        }

        // Check for PPolicy data

        PasswordPolicyResponseControl ppolicy = (PasswordPolicyResponseControl) ctx.getObjectAttribute(PasswordPolicyControl.OID);

        if (ppolicy != null) {
            essence.setTimeBeforeExpiration(ppolicy.getTimeBeforeExpiration());
            essence.setGraceLoginsRemaining(ppolicy.getGraceLoginsRemaining());
        }

        return (LdapUserDetailsImpl) essence.createUserDetails();

    }
}
