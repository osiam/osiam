package org.osiam.auth.login.ldap;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.osiam.auth.exception.LdapAuthenticationProcessException;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.osiam.resources.scim.Email.Type;
import org.osiam.security.authentication.OsiamUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;

import com.google.common.base.Strings;

public class OsiamLdapUserSynchronizer {

    private final String LDAP_PROVIDER = "ldap";
    private final String AUTH_EXTENSION = "urn:scim:schemas:osiam:2.0:authentication:server";

    @Inject
    private OsiamUserDetailsService userDetailsService;

    @Value("${org.osiam.auth.ldap.sync.user.data:true}")
    private boolean syncUserData;

    public OsiamLdapUserDetailsImpl synchroniseLdapData(DirContextOperations ldapUserData, OsiamLdapUserDetailsImpl ldapUser) {
        String userName = ldapUserData.getStringAttribute("uid");

        User user = userDetailsService.getUserByUsername(userName);

        boolean userExists = user != null;

        String displayName = ldapUserData.getStringAttribute("displayName");
        String email = ldapUserData.getStringAttribute("mail");

        if (userExists && (!user.isExtensionPresent(AUTH_EXTENSION)
                || !user.getExtension(AUTH_EXTENSION).isFieldPresent("origin")
                || !user.getExtension(AUTH_EXTENSION).getFieldAsString("origin").equals(LDAP_PROVIDER))) {

            throw new LdapAuthenticationProcessException("Can't create the '" + LDAP_PROVIDER
                    + "' user with the username '"
                    + userName + "'. An internal user with the same username exists.");
        }

        if (!userExists) {
            Extension extension = new Extension(AUTH_EXTENSION);
            extension.addOrUpdateField("origin", LDAP_PROVIDER);

            User.Builder builder = new User.Builder(userName)
                    .setDisplayName(displayName)
                    .setName(createName(ldapUserData))
                    .addExtension(extension)
                    .setActive(true);

            if (!Strings.isNullOrEmpty(email)) {
                Email.Builder emailBuilder = new Email.Builder().setValue(email).setType(new Type(LDAP_PROVIDER));
                List<Email> emails = new ArrayList<Email>();
                emails.add(emailBuilder.build());
                builder.setEmails(emails);
            }

            user = userDetailsService.createUser(builder.build());

        } else if (syncUserData && userExists) {
            UpdateUser.Builder updateUserBuilder = new UpdateUser.Builder()
                    .updateName(createName(ldapUserData))
                    .updateDisplayName(displayName);

            updateEmail(updateUserBuilder, user.getEmails(), email);

            user = userDetailsService.updateUser(user.getId(), updateUserBuilder.build());
        }

        ldapUser.setId(user.getId());
        return ldapUser;
    }

    private void updateEmail(UpdateUser.Builder updateBuilder, List<Email> emails, String emailValue) {
        Email newEmail = new Email.Builder().setValue(emailValue).setType(new Type(LDAP_PROVIDER)).build();
        for (Email email : emails) {
            if (email.getType() != null && email.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deleteEmail(email);
            }
        }
        updateBuilder.addEmail(newEmail);
    }

    private Name createName(DirContextOperations userData) {
        String givenName = userData.getStringAttribute("givenName");
        String familyName = userData.getStringAttribute("sn");
        Name name = null;

        if (!Strings.isNullOrEmpty(givenName) || !Strings.isNullOrEmpty(familyName)) {
            name = new Name.Builder()
                    .setGivenName(givenName)
                    .setFamilyName(familyName)
                    .build();
        }

        return name;
    }
}
