package org.osiam.resources.helper;

import org.osiam.resources.scim.*;
import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.dao.UserDAO;
import org.osiam.storage.entities.*;
import org.osiam.storage.entities.extension.ExtensionEntity;
import org.osiam.storage.entities.extension.ExtensionFieldEntity;
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ScimConverter {

    @Inject
    private ExtensionDao extensionDao;

    @Inject
    private UserDAO userDao;

    public User toScim(UserEntity userEntity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public UserEntity createFromScim(User user) {


        UserEntity userEntity = new UserEntity();
        if (user.getId() != null) {
            try {
                userEntity = userDao.getById(user.getId());
            } catch (NoResultException ex) {
                // don't know what to do here
            }
            userEntity.setId(UUID.fromString(user.getId()));
        }

        if (!(user.getPassword() == null || user.getPassword().isEmpty())){
            userEntity.setPassword(user.getPassword());
        }

        userEntity.setActive(user.isActive());
        userEntity.setDisplayName(user.getDisplayName());
        userEntity.setNickName(user.getNickName());
        userEntity.setExternalId(user.getExternalId() == null ? null : user.getExternalId().equals("") ? null : user.getExternalId()); //Due to uniqueness in databases
        userEntity.setName(scimNameToEntity(user.getName()));
        userEntity.setPreferredLanguage(user.getPreferredLanguage());
        userEntity.setLocale(user.getLocale());

        userEntity.getAddresses().clear();
        userEntity.getAddresses().addAll(scimUserAddressesToEntity(user.getAddresses()));

        userEntity.getEmails().clear();
        userEntity.getEmails().addAll(scimEmailsToEntity(user.getEmails()));

        userEntity.getEntitlements().clear();
        userEntity.getEntitlements().addAll(scimEntitlementsToEntity(user.getEntitlements()));

        userEntity.getIms().clear();
        userEntity.getIms().addAll(scimImsToEntity(user.getIms()));

        userEntity.getPhoneNumbers().clear();
        userEntity.getPhoneNumbers().addAll(scimPhonenumbersToEntity(user.getPhoneNumbers()));

        userEntity.getPhotos().clear();
        userEntity.getPhotos().addAll(scimPhotosToEntity(user.getPhotos()));

        userEntity.getRoles().clear();
        userEntity.getRoles().addAll(scimUserRolesToEntity(user.getRoles()));

        userEntity.getX509Certificates().clear();
        userEntity.getX509Certificates().addAll(scimCertificatesToEntity(user.getX509Certificates()));

        userEntity.getUserExtensions().clear();
        userEntity.getUserExtensions().addAll(scimExtensionsToEntity(user, userEntity));

        userEntity.setProfileUrl(user.getProfileUrl());
        userEntity.setTimezone(user.getTimezone());
        userEntity.setTitle(user.getTitle());
        userEntity.setUserName(user.getUserName());
        userEntity.setUserType(user.getUserType());


        return userEntity;
    }


    private Set<ExtensionFieldValueEntity> scimExtensionsToEntity(User scimUser, UserEntity userEntity) {
        Set<ExtensionFieldValueEntity> extensionFieldValueEntities = new HashSet<>();

        Set<String> userExtensionUris = scimUser.getAllExtensions().keySet();
        for (String urn : userExtensionUris) {
            Extension scimExtension = scimUser.getExtension(urn);
            ExtensionEntity extensionEntity = extensionDao.getExtensionByUrn(scimExtension.getUrn());
            Set<ExtensionFieldValueEntity> extensionFieldValues = mappingScimUserExtensionToEntity(scimExtension, userEntity, extensionEntity);
            addExtensionUrnToExtensionFields(extensionFieldValues, urn, extensionEntity);
            extensionFieldValueEntities.addAll(extensionFieldValues);
        }
        return extensionFieldValueEntities;
    }

    private Set<ExtensionFieldValueEntity> mappingScimUserExtensionToEntity(Extension scimExtension, UserEntity userEntity, ExtensionEntity extensionEntity) {
        Set<ExtensionFieldValueEntity> extensionFieldValueEntities = new HashSet<>();

        for (ExtensionFieldEntity extensionFieldEntity : extensionEntity.getFields()) {
            ExtensionFieldValueEntity extensionFieldValueEntity = new ExtensionFieldValueEntity();
            extensionFieldValueEntity.setExtensionField(extensionFieldEntity);
            extensionFieldValueEntity.setUser(userEntity);

            if (scimExtension.isFieldPresent(extensionFieldEntity.getName())) {
                String extensionFieldValue = scimExtension.getField(extensionFieldEntity.getName());
                extensionFieldValueEntity.setValue(extensionFieldValue);
            } else {
                extensionFieldValueEntity.setValue("");
            }

            extensionFieldValueEntities.add(extensionFieldValueEntity);
        }

        return extensionFieldValueEntities;
    }

    private void addExtensionUrnToExtensionFields(Set<ExtensionFieldValueEntity> extensionFieldValueEntities, String urn, ExtensionEntity extensionEntity) {
        Set<ExtensionFieldEntity> extensionFieldEntitySet = new HashSet<>();

        for (ExtensionFieldValueEntity extensionFieldValueEntity : extensionFieldValueEntities) {
            extensionFieldEntitySet.add(extensionFieldValueEntity.getExtensionField());
        }

        extensionEntity.setUrn(urn);
        extensionEntity.setFields(extensionFieldEntitySet);

    }

    private Set<X509CertificateEntity> scimCertificatesToEntity(List<MultiValuedAttribute> x509Certificates) {
        Set<X509CertificateEntity> x509CertificateEntities = new HashSet<>();
        if (x509Certificates != null) {
            for (MultiValuedAttribute multiValuedAttribute : x509Certificates) {
                x509CertificateEntities.add(X509CertificateEntity.fromScim(multiValuedAttribute));
            }
        }
        return x509CertificateEntities;
    }

    private Set<RolesEntity> scimUserRolesToEntity(List<MultiValuedAttribute> roles) {
        Set<RolesEntity> rolesEntities = new HashSet<>();
        if (roles != null) {
            for (MultiValuedAttribute multiValuedAttribute : roles) {
                rolesEntities.add(RolesEntity.fromScim(multiValuedAttribute));
            }
        }
        return rolesEntities;
    }

    private Set<PhotoEntity> scimPhotosToEntity(List<MultiValuedAttribute> photos) {
        Set<PhotoEntity> photoEntities = new HashSet<>();
        if (photos != null) {
            for (MultiValuedAttribute multiValuedAttribute : photos) {
                photoEntities.add(PhotoEntity.fromScim(multiValuedAttribute));
            }
        }
        return photoEntities;
    }

    private Set<PhoneNumberEntity> scimPhonenumbersToEntity(List<MultiValuedAttribute> phoneNumbers) {
        Set<PhoneNumberEntity> phoneNumberEntities = new HashSet<>();
        if (phoneNumbers != null) {
            for (MultiValuedAttribute multiValuedAttribute : phoneNumbers) {
                phoneNumberEntities.add(PhoneNumberEntity.fromScim(multiValuedAttribute));
            }
        }
        return phoneNumberEntities;
    }

    private NameEntity scimNameToEntity(Name name) {
        return NameEntity.fromScim(name);
    }

    private Set<ImEntity> scimImsToEntity(List<MultiValuedAttribute> ims) {
        Set<ImEntity> imEntities = new HashSet<>();
        if (ims != null) {
            for (MultiValuedAttribute multiValuedAttribute : ims) {
                imEntities.add(ImEntity.fromScim(multiValuedAttribute));
            }
        }
        return imEntities;
    }

    private Set<EntitlementsEntity> scimEntitlementsToEntity(List<MultiValuedAttribute> entitlements) {
        Set<EntitlementsEntity> entitlementsEntities = new HashSet<>();
        if (entitlements != null) {
            for (MultiValuedAttribute multiValuedAttribute : entitlements) {
                entitlementsEntities.add(EntitlementsEntity.fromScim(multiValuedAttribute));
            }
        }
        return entitlementsEntities;
    }

    private Set<AddressEntity> scimUserAddressesToEntity(List<Address> addresses) {

        Set<AddressEntity> addressEntities = new HashSet<>();
        if (addresses != null) {
            for (Address address : addresses) {
                addressEntities.add(AddressEntity.fromScim(address));
            }
        }
        return addressEntities;
    }

    private Set<EmailEntity> scimEmailsToEntity(List<MultiValuedAttribute> emails) {
        Set<EmailEntity> emailEntities = new HashSet<>();
        if (emails != null) {
            for (MultiValuedAttribute multiValuedAttribute : emails) {
                emailEntities.add(EmailEntity.fromScim(multiValuedAttribute));
            }
        }
        return emailEntities;
    }

}
