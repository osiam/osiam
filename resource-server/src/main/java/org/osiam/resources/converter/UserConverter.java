package org.osiam.resources.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;
import org.osiam.storage.entities.AddressEntity;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.EntitlementsEntity;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.NameEntity;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.PhotoEntity;
import org.osiam.storage.entities.RolesEntity;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.X509CertificateEntity;
import org.osiam.storage.entities.extension.ExtensionEntity;
import org.springframework.stereotype.Service;

@Service
public class UserConverter implements Converter<User, UserEntity> {

    @Inject
    private Converter<MultiValuedAttribute, X509CertificateEntity> x509CertificateConverter;
    @Inject
    private Converter<MultiValuedAttribute, RolesEntity> roleConverter;
    @Inject
    private Converter<MultiValuedAttribute, PhotoEntity> photoConverter;
    @Inject
    private Converter<MultiValuedAttribute, PhoneNumberEntity> phoneNumberConverter;
    @Inject
    private Converter<MultiValuedAttribute, ImEntity> imConverter;
    @Inject
    private Converter<MultiValuedAttribute, EntitlementsEntity> entitlementConverter;
    @Inject
    private Converter<MultiValuedAttribute, EmailEntity> emailConverter;
    @Inject
    private Converter<Address, AddressEntity> addressConverter;
    @Inject
    private Converter<Name, NameEntity> nameConverter;
    @Inject
    private Converter<Extension, ExtensionEntity> extensionConverter;

    @Override
    public UserEntity fromScim(User scim) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User toScim(UserEntity entity) {
        User.Builder userBuilder = new User.Builder(entity.getUserName()).setActive(entity.getActive())
                .setDisplayName(entity.getDisplayName()).setLocale(entity.getLocale())
                .setName(entity.getName() != null ? nameConverter.toScim(entity.getName()) : null)
                .setNickName(entity.getNickName()).setPassword(entity.getPassword())
                .setPreferredLanguage(entity.getPreferredLanguage()).setProfileUrl(entity.getProfileUrl())
                .setTimezone(entity.getTimezone()).setTitle(entity.getTitle()).setUserType(entity.getUserType())
                .setExternalId(entity.getExternalId()).setId(entity.getId().toString())
                .setMeta(entity.getMeta().toScim()).setAddresses(entityAddressToScim(entity.getAddresses()))
                .setEmails(entityEmailToScim(entity.getEmails()))
                .setEntitlements(entityEntitlementsToScim(entity.getEntitlements()))
                .setGroups(entityGroupsToScim(entity.getGroups())).setIms(entityImsToScim(entity.getIms()))
                .setPhoneNumbers(entityPhonenumbersToScim(entity.getPhoneNumbers()))
                .setPhotos(entityPhotosToScim(entity.getPhotos())).setRoles(entityRolesToScim(entity.getRoles()))
                .setX509Certificates(entityX509CertificatesToScim(entity.getX509Certificates()));

        return userBuilder.build();

    }

    private List<Address> entityAddressToScim(Set<AddressEntity> addressEntities) {
        List<Address> addressesForMapping = new ArrayList<>();
        for (AddressEntity addressEntity : addressEntities) {
            addressesForMapping.add(addressConverter.toScim(addressEntity));
        }
        return addressesForMapping;
    }

    private List<MultiValuedAttribute> entityEmailToScim(Set<EmailEntity> emailEntities) {
        List<MultiValuedAttribute> emailsForMapping = new ArrayList<>();
        for (EmailEntity emailEntity : emailEntities) {
            emailsForMapping.add(emailConverter.toScim(emailEntity));
        }
        return emailsForMapping;
    }

    private List<MultiValuedAttribute> entityEntitlementsToScim(Set<EntitlementsEntity> entitlementsEntities) {
        List<MultiValuedAttribute> entitlementsForMapping = new ArrayList<>();
        for (EntitlementsEntity entitlementsEntity : entitlementsEntities) {
            entitlementsForMapping.add(entitlementConverter.toScim(entitlementsEntity));
        }
        return entitlementsForMapping;
    }

    private List<MultiValuedAttribute> entityGroupsToScim(Set<GroupEntity> groupEntities) {
        List<MultiValuedAttribute> groupsForMapping = new ArrayList<>();
        for (GroupEntity groupEntity : groupEntities) {
            groupsForMapping.add(groupEntity.toMultiValueScim());
        }
        return groupsForMapping;
    }

    private List<MultiValuedAttribute> entityImsToScim(Set<ImEntity> imEntities) {
        List<MultiValuedAttribute> imsForMapping = new ArrayList<>();
        for (ImEntity imEntity : imEntities) {
            imsForMapping.add(imConverter.toScim(imEntity));
        }
        return imsForMapping;
    }

    private List<MultiValuedAttribute> entityPhonenumbersToScim(Set<PhoneNumberEntity> phoneNumberEntities) {
        List<MultiValuedAttribute> phoneNumbersForMapping = new ArrayList<>();
        for (PhoneNumberEntity phoneNumberEntity : phoneNumberEntities) {
            phoneNumbersForMapping.add(phoneNumberConverter.toScim(phoneNumberEntity));
        }
        return phoneNumbersForMapping;
    }

    private List<MultiValuedAttribute> entityPhotosToScim(Set<PhotoEntity> photoEntities) {
        List<MultiValuedAttribute> photosForMapping = new ArrayList<>();
        for (PhotoEntity photoEntity : photoEntities) {
            photosForMapping.add(photoConverter.toScim(photoEntity));
        }
        return photosForMapping;
    }

    private List<MultiValuedAttribute> entityRolesToScim(Set<RolesEntity> rolesEntities) {
        List<MultiValuedAttribute> rolesForMapping = new ArrayList<>();
        for (RolesEntity rolesEntity : rolesEntities) {
            rolesForMapping.add(roleConverter.toScim(rolesEntity));
        }
        return rolesForMapping;
    }

    private List<MultiValuedAttribute> entityX509CertificatesToScim(Set<X509CertificateEntity> x509CertificateEntities) {
        List<MultiValuedAttribute> x509CertificatesForMapping = new ArrayList<>();
        for (X509CertificateEntity x509CertificateEntity : x509CertificateEntities) {
            x509CertificatesForMapping.add(x509CertificateConverter.toScim(x509CertificateEntity));
        }
        return x509CertificatesForMapping;
    }

}
