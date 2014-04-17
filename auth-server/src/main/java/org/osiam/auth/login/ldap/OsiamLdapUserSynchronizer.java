package org.osiam.auth.login.ldap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.naming.ConfigurationException;

import org.osiam.auth.exception.LdapAuthenticationProcessException;
import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Email;
import org.osiam.resources.scim.Entitlement;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.Im;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.PhoneNumber;
import org.osiam.resources.scim.Photo;
import org.osiam.resources.scim.Role;
import org.osiam.resources.scim.UpdateUser;
import org.osiam.resources.scim.User;
import org.osiam.resources.scim.X509Certificate;
import org.osiam.security.authentication.OsiamUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;

public class OsiamLdapUserSynchronizer {

    private final String LDAP_PROVIDER = "ldap";
    private final String AUTH_EXTENSION = "urn:scim:schemas:osiam:2.0:authentication:server";

    @Inject
    private OsiamUserDetailsService userDetailsService;

    @Value("${org.osiam.auth.ldap.sync.user.data:true}")
    private boolean syncUserData;

    private Map<String, String> scimLdapAttributes;

    public OsiamLdapUserSynchronizer(Map<String, String> scimLdapAttributes) {
        this.scimLdapAttributes = scimLdapAttributes;
    }

    public OsiamLdapUserDetailsImpl synchroniseLdapData(DirContextOperations ldapUserData,
            OsiamLdapUserDetailsImpl ldapUser) {
        String userName = ldapUserData.getStringAttribute(scimLdapAttributes.get("userName"));

        User user = userDetailsService.getUserByUsername(userName);

        boolean userExists = user != null;

        if (userExists && (!user.isExtensionPresent(AUTH_EXTENSION)
                || !user.getExtension(AUTH_EXTENSION).isFieldPresent("origin")
                || !user.getExtension(AUTH_EXTENSION).getFieldAsString("origin").equals(LDAP_PROVIDER))) {

            throw new LdapAuthenticationProcessException("Can't create the '" + LDAP_PROVIDER
                    + "' user with the username '"
                    + userName + "'. An internal user with the same username exists.");
        }

        if (!userExists) {
            user = createUser(userName, ldapUserData);
        } else if (syncUserData && userExists) {
            user = updateUser(user, ldapUserData);
        }

        ldapUser.setId(user.getId());
        return ldapUser;
    }

    private User createUser(String userName, DirContextOperations ldapUserData) {

        Extension extension = new Extension(AUTH_EXTENSION);
        extension.addOrUpdateField("origin", LDAP_PROVIDER);

        User.Builder builder = new User.Builder(userName)
                .addExtension(extension)
                .setActive(true);

        for (String scimAttribute : scimLdapAttributes.keySet()) {
            String ldapAttribute = scimLdapAttributes.get(scimAttribute);
            String ldapValue = ldapUserData.getStringAttribute(ldapAttribute);

            switch (scimAttribute) {
            case "displayName":
                builder.setDisplayName(ldapValue);
                break;
            case "email":
                Email.Builder emailBuilder = new Email.Builder().setValue(ldapValue)
                        .setType(new Email.Type(LDAP_PROVIDER));
                List<Email> emails = new ArrayList<Email>();
                emails.add(emailBuilder.build());
                builder.setEmails(emails);
                break;
            case "entitlement":
                Entitlement.Builder entitlementBuilder = new Entitlement.Builder().setValue(ldapValue)
                        .setType(new Entitlement.Type(LDAP_PROVIDER));
                List<Entitlement> entitlements = new ArrayList<Entitlement>();
                entitlements.add(entitlementBuilder.build());
                builder.setEntitlements(entitlements);
                break;
            case "externalId":
                builder.setExternalId(ldapValue);
                break;
            case "im":
                Im.Builder imBuilder = new Im.Builder().setValue(ldapValue)
                        .setType(new Im.Type(LDAP_PROVIDER));
                List<Im> ims = new ArrayList<Im>();
                ims.add(imBuilder.build());
                builder.setIms(ims);
                break;
            case "locale":
                builder.setLocale(ldapValue);
                break;
            case "nickName":
                builder.setNickName(ldapValue);
                break;
            case "phoneNumber":
                PhoneNumber.Builder phoneNumberBuilder = new PhoneNumber.Builder().setValue(ldapValue)
                        .setType(new PhoneNumber.Type(LDAP_PROVIDER));
                List<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();
                phoneNumbers.add(phoneNumberBuilder.build());
                builder.setPhoneNumbers(phoneNumbers);
                break;
            case "photo":
                Photo.Builder photoBuilder;
                try {
                    photoBuilder = new Photo.Builder().setValue(new URI(ldapValue))
                            .setType(new Photo.Type(LDAP_PROVIDER));
                    List<Photo> photos = new ArrayList<Photo>();
                    photos.add(photoBuilder.build());
                    builder.setPhotos(photos);
                } catch (URISyntaxException e) {
                    new ConfigurationException("Could not map the ldap attibute '"
                            + ldapAttribute + "' with the value '" + ldapValue
                            + "' into an scim photo because the value could not be conferted into an URI.");
                }
                break;
            case "preferredLanguage":
                builder.setPreferredLanguage(ldapValue);
                break;
            case "profileUrl":
                builder.setProfileUrl(ldapValue);
                break;
            case "role":
                Role.Builder roleBuilder = new Role.Builder().setValue(ldapValue)
                        .setType(new Role.Type(LDAP_PROVIDER));
                List<Role> roles = new ArrayList<Role>();
                roles.add(roleBuilder.build());
                builder.setRoles(roles);
                break;
            case "timezone":
                builder.setTimezone(ldapValue);
                break;
            case "title":
                builder.setTitle(ldapValue);
                break;
            case "userType":
                builder.setUserType(ldapValue);
                break;
            case "x509Certificate":
                X509Certificate.Builder x509CertificateBuilder = new X509Certificate.Builder().setValue(ldapValue)
                        .setType(new X509Certificate.Type(LDAP_PROVIDER));
                List<X509Certificate> x509Certificates = new ArrayList<X509Certificate>();
                x509Certificates.add(x509CertificateBuilder.build());
                builder.setX509Certificates(x509Certificates);
                break;
            default:
                if (!scimAttribute.startsWith("address.") && !scimAttribute.startsWith("name.")) {
                    new ConfigurationException("The ldap attibute mapping value '" + scimAttribute
                            + "' could not be reconized as scim attribute.");
                }
                break;
            }
        }

        builder.setAddresses(getAddresses(ldapUserData));
        builder.setName(getName(ldapUserData));

        User user = userDetailsService.createUser(builder.build());
        return user;
    }

    private User updateUser(User user, DirContextOperations ldapUserData) {

        UpdateUser.Builder updateBuilder = new UpdateUser.Builder();

        for (String scimAttribute : scimLdapAttributes.keySet()) {
            String ldapValue = ldapUserData.getStringAttribute(scimLdapAttributes.get(scimAttribute));

            switch (scimAttribute) {
            case "displayName":
                updateBuilder.updateDisplayName(ldapValue);
                break;
            case "email":
                updateEmail(updateBuilder, user.getEmails(), ldapValue);
                break;
            case "entitlement":
                updateEntitlement(updateBuilder, user.getEntitlements(), ldapValue);
                break;
            case "externalId":
                updateBuilder.updateExternalId(ldapValue);
                break;
            case "im":
                updateIm(updateBuilder, user.getIms(), ldapValue);
                break;
            case "locale":
                updateBuilder.updateLocale(ldapValue);
                break;
            case "nickName":
                updateBuilder.updateNickName(ldapValue);
                break;
            case "phoneNumber":
                updatePhoneNumber(updateBuilder, user.getPhoneNumbers(), ldapValue);
                break;
            case "photo":
                updatePhoto(updateBuilder, user.getPhotos(), ldapValue, scimAttribute);
                break;
            case "preferredLanguage":
                updateBuilder.updatePreferredLanguage(ldapValue);
                break;
            case "profileUrl":
                updateBuilder.updateProfileUrl(ldapValue);
                break;
            case "role":
                updateRole(updateBuilder, user.getRoles(), ldapValue);
                break;
            case "timezone":
                updateBuilder.updateTimezone(ldapValue);
                break;
            case "title":
                updateBuilder.updateTitle(ldapValue);
                break;
            case "userType":
                updateBuilder.updateUserType(ldapValue);
                break;
            case "x509Certificate":
                updateX509Certificate(updateBuilder, user.getX509Certificates(), ldapValue);
                break;
            default:
                if (!scimAttribute.startsWith("address.") && !scimAttribute.startsWith("name.")) {
                    new ConfigurationException("The ldap attibute mapping value '" + scimAttribute
                            + "' could not be reconized as scim attribute.");
                }
            }
        }

        updateAddress(updateBuilder, user.getAddresses(), ldapUserData);
        updateName(updateBuilder, ldapUserData);

        user = userDetailsService.updateUser(user.getId(), updateBuilder.build());
        return user;
    }

    private void updateName(UpdateUser.Builder updateBuilder, DirContextOperations ldapUserData) {
        updateBuilder.updateName(getName(ldapUserData));
    }

    private void updateAddress(UpdateUser.Builder updateBuilder, List<Address> addresses,
            DirContextOperations ldapUserData) {
        for (Address address : addresses) {
            if (address.getType() != null && address.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deleteAddress(address);
            }
        }
        updateBuilder.addAddress(getAddresses(ldapUserData).get(0));
    }

    private void updateEmail(UpdateUser.Builder updateBuilder, List<Email> emails, String emailValue) {
        Email newEmail = new Email.Builder().setValue(emailValue).setType(new Email.Type(LDAP_PROVIDER)).build();
        for (Email email : emails) {
            if (email.getType() != null && email.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deleteEmail(email);
            }
        }
        updateBuilder.addEmail(newEmail);
    }

    private void updateEntitlement(UpdateUser.Builder updateBuilder, List<Entitlement> entitlements, String value) {
        Entitlement newEntitlement = new Entitlement.Builder().setValue(value)
                .setType(new Entitlement.Type(LDAP_PROVIDER)).build();
        for (Entitlement entitlement : entitlements) {
            if (entitlement.getType() != null && entitlement.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deleteEntitlement(entitlement);
            }
        }
        updateBuilder.addEntitlement(newEntitlement);
    }

    private void updateIm(UpdateUser.Builder updateBuilder, List<Im> ims, String value) {
        Im newIm = new Im.Builder().setValue(value).setType(new Im.Type(LDAP_PROVIDER)).build();
        for (Im im : ims) {
            if (im.getType() != null && im.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deleteIms(im);
            }
        }
        updateBuilder.addIms(newIm);
    }

    private void updatePhoneNumber(UpdateUser.Builder updateBuilder, List<PhoneNumber> phoneNumbers, String value) {
        PhoneNumber newPhoneNumber = new PhoneNumber.Builder().setValue(value)
                .setType(new PhoneNumber.Type(LDAP_PROVIDER)).build();
        for (PhoneNumber phoneNumber : phoneNumbers) {
            if (phoneNumber.getType() != null && phoneNumber.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deletePhoneNumber(phoneNumber);
            }
        }
        updateBuilder.addPhoneNumber(newPhoneNumber);
    }

    private void updatePhoto(UpdateUser.Builder updateBuilder, List<Photo> photos, String value, String scimAttribute) {
        try {
            Photo newPhoto = new Photo.Builder().setValue(new URI(value)).setType(new Photo.Type(LDAP_PROVIDER))
                    .build();
            for (Photo photo : photos) {
                if (photo.getType() != null && photo.getType().toString().equals(LDAP_PROVIDER)) {
                    updateBuilder.deletePhoto(photo);
                }
            }
            updateBuilder.addPhoto(newPhoto);
        } catch (URISyntaxException e) {
            new ConfigurationException("Could not map the ldap attibute '"
                    + scimLdapAttributes.get(scimAttribute) + "' with the value '" + value
                    + "' into an scim photo because the value could not be conferted into an URI.");
        }
    }

    private void updateRole(UpdateUser.Builder updateBuilder, List<Role> roles, String value) {
        Role newRole = new Role.Builder().setValue(value).setType(new Role.Type(LDAP_PROVIDER)).build();
        for (Role role : roles) {
            if (role.getType() != null && role.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deleteRole(role);
            }
        }
        updateBuilder.addRole(newRole);
    }

    private void updateX509Certificate(UpdateUser.Builder updateBuilder, List<X509Certificate> x509Certificates,
            String value) {
        X509Certificate newX509Certificate = new X509Certificate.Builder().setValue(value)
                .setType(new X509Certificate.Type(LDAP_PROVIDER)).build();
        for (X509Certificate x509Certificate : x509Certificates) {
            if (x509Certificate.getType() != null && x509Certificate.getType().toString().equals(LDAP_PROVIDER)) {
                updateBuilder.deleteX509Certificate(x509Certificate);
            }
        }
        updateBuilder.addX509Certificate(newX509Certificate);
    }

    private List<Address> getAddresses(DirContextOperations ldapUserData) {
        List<Address> addresses = null;
        Address.Builder builder = new Address.Builder();
        boolean addressFound = false;

        for (String scimAttribute : scimLdapAttributes.keySet()) {
            String ldapValue = ldapUserData.getStringAttribute(scimLdapAttributes.get(scimAttribute));
            if (!scimAttribute.startsWith("address.")) {
                continue;
            }
            addressFound = true;
            switch (scimAttribute) {
            case "address.country":
                builder.setCountry(ldapValue);
                break;
            case "address.formatted":
                builder.setFormatted(ldapValue);
                break;
            case "address.locality":
                builder.setLocality(ldapValue);
                break;
            case "address.postalCode":
                builder.setPostalCode(ldapValue);
                break;
            case "address.region":
                builder.setRegion(ldapValue);
                break;
            case "address.streetAddress":
                builder.setStreetAddress(ldapValue);
                break;
            default:
                new ConfigurationException("The ldap attibute mapping value '" + scimAttribute
                        + "' could not be reconized as scim attribute.");
                break;
            }
        }

        if (addressFound) {
            builder.setType(new Address.Type(LDAP_PROVIDER));

            addresses = new ArrayList<Address>();
            addresses.add(builder.build());
        }
        return addresses;
    }

    private Name getName(DirContextOperations ldapUserData) {
        Name name = null;
        Name.Builder builder = new Name.Builder();
        boolean nameFound = false;

        for (String scimAttribute : scimLdapAttributes.keySet()) {
            String ldapValue = ldapUserData.getStringAttribute(scimLdapAttributes.get(scimAttribute));
            if (!scimAttribute.startsWith("name.")) {
                continue;
            }
            nameFound = true;
            switch (scimAttribute) {
            case "name.familyName":
                builder.setFamilyName(ldapValue);
                break;
            case "name.formatted":
                builder.setFormatted(ldapValue);
                break;
            case "name.givenName":
                builder.setGivenName(ldapValue);
                break;
            case "name.honorificPrefix":
                builder.setHonorificPrefix(ldapValue);
                break;
            case "name.honorificSuffix":
                builder.setHonorificSuffix(ldapValue);
                break;
            case "name.middleName":
                builder.setMiddleName(ldapValue);
                break;
            default:
                new ConfigurationException("The ldap attibute mapping value '" + scimAttribute
                        + "' could not be reconized as scim attribute.");
                break;
            }
        }

        if (nameFound) {
            name = builder.build();
        }
        return name;
    }

}
