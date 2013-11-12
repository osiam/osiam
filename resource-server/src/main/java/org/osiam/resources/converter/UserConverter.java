package org.osiam.resources.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.User;
import org.osiam.storage.dao.UserDAO;
import org.osiam.storage.entities.AddressEntity;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.EntitlementsEntity;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.PhotoEntity;
import org.osiam.storage.entities.RolesEntity;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.X509CertificateEntity;
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity;
import org.springframework.stereotype.Service;

@Service
public class UserConverter implements Converter<User, UserEntity> {

    @Inject
    private X509CertificateConverter x509CertificateConverter;
    @Inject
    private RoleConverter roleConverter;
    @Inject
    private PhotoConverter photoConverter;
    @Inject
    private PhoneNumberConverter phoneNumberConverter;
    @Inject
    private ImConverter imConverter;
    @Inject
    private EntitlementConverter entitlementConverter;
    @Inject
    private EmailConverter emailConverter;
    @Inject
    private AddressConverter addressConverter;
    @Inject
    private NameConverter nameConverter;
    @Inject
    private ExtensionConverter extensionConverter;
    @Inject
    private MetaConverter metaConverter;
    @Inject
    private UserDAO userDao;
    
    @Override
    public UserEntity fromScim(User scim) {
        if(scim == null){
            return null;
        }
        UserEntity userEntity = new UserEntity();
        try {
            UserEntity existingEntity = userDao.getById(scim.getId());
            userEntity.setInternalId(existingEntity.getInternalId());
            userEntity.setMeta(existingEntity.getMeta());
            userEntity.setPassword(existingEntity.getPassword());
        } catch (NoResultException ex) {
            // Just It's a new one.
        }
        userEntity.setId(UUID.fromString(scim.getId()));
        return copyUserValues(scim, userEntity);
       
    }
    
    private UserEntity copyUserValues(User user, UserEntity userEntity) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userEntity.setPassword(user.getPassword());
        }

        userEntity.setActive(user.isActive());
        userEntity.setDisplayName(user.getDisplayName());
        userEntity.setNickName(user.getNickName());
        userEntity.setExternalId(user.getExternalId() == null ? null : user.getExternalId().equals("") ? null : user.getExternalId()); //Due to uniqueness in databases
        userEntity.setPreferredLanguage(user.getPreferredLanguage());
        userEntity.setLocale(user.getLocale());
        userEntity.setProfileUrl(user.getProfileUrl());
        userEntity.setTimezone(user.getTimezone());
        userEntity.setTitle(user.getTitle());
        userEntity.setUserName(user.getUserName());
        userEntity.setUserType(user.getUserType());
        
        userEntity.setName(nameConverter.fromScim(user.getName()));
        
        userEntity.setAddresses(convertMultiValue(addressConverter, new HashSet<>(user.getAddresses())));
        userEntity.setEmails(convertMultiValue(emailConverter, new HashSet<>(user.getEmails())));
        userEntity.setEntitlements(convertMultiValue(entitlementConverter, new HashSet<>(user.getEntitlements())));
        userEntity.setIms(convertMultiValue(imConverter, new HashSet<>(user.getIms())));
        userEntity.setPhoneNumbers(convertMultiValue(phoneNumberConverter, new HashSet<>(user.getPhoneNumbers())));
        userEntity.setPhotos(convertMultiValue(photoConverter, new HashSet<>(user.getPhotos())));
        userEntity.setRoles(convertMultiValue(roleConverter, new HashSet<>(user.getRoles())));
        userEntity.setX509Certificates(convertMultiValue(x509CertificateConverter, new HashSet<>(user.getX509Certificates())));
        userEntity.setUserExtensions(convertExtension(new HashSet<>(user.getAllExtensions().values())));
                
        return userEntity;
    }

    private Set<ExtensionFieldValueEntity> convertExtension(Set<Extension> extensions){
        Set<ExtensionFieldValueEntity> entities = extensionConverter.fromScim(extensions);
        return entities;
    }
    
    private <S, E> Set<E> convertMultiValue(Converter<S, E> converter, Set<S> multiValues){
        Set<E> entities = new HashSet<>();
        
        for (S multiValue : multiValues) {
            E entity = converter.fromScim(multiValue);
            entities.add(entity);
        }
        return entities;
    }
    
    @Override
    public User toScim(UserEntity entity) {
        if(entity == null){
            return null;
        }
        User.Builder userBuilder = new User.Builder(entity.getUserName())
                .setActive(entity.getActive())
                .setDisplayName(entity.getDisplayName())
                .setLocale(entity.getLocale())
                .setName(entity.getName() != null ? nameConverter.toScim(entity.getName()) : null)
                .setNickName(entity.getNickName())
                .setPassword(entity.getPassword())
                .setPreferredLanguage(entity.getPreferredLanguage())
                .setProfileUrl(entity.getProfileUrl())
                .setTimezone(entity.getTimezone())
                .setTitle(entity.getTitle())
                .setUserType(entity.getUserType())
                .setExternalId(entity.getExternalId())
                .setId(entity.getId().toString())
                .setMeta(metaConverter.toScim(entity.getMeta()))
                .setAddresses(entityAddressToScim(entity.getAddresses()))
                .setEmails(entityEmailToScim(entity.getEmails()))
                .setEntitlements(entityEntitlementsToScim(entity.getEntitlements()))
                .setGroups(entityGroupsToScim(entity.getGroups()))
                .setIms(entityImsToScim(entity.getIms()))
                .setPhoneNumbers(entityPhonenumbersToScim(entity.getPhoneNumbers()))
                .setPhotos(entityPhotosToScim(entity.getPhotos()))
                .setRoles(entityRolesToScim(entity.getRoles()))
                .setX509Certificates(entityX509CertificatesToScim(entity.getX509Certificates()));

        addExtensionField(userBuilder, entity.getUserExtensions());

        return userBuilder.build();

    }

    private void addExtensionField(User.Builder userBuilder, Set<ExtensionFieldValueEntity> extensionEntities) {

        Set<Extension> extensions = extensionConverter.toScim(extensionEntities);

        for (Extension extension : extensions) {
            userBuilder.addExtension(extension.getUrn(), extension);
        }
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
            MultiValuedAttribute multiValue = new MultiValuedAttribute.Builder()
                    .setDisplay(groupEntity.getDisplayName())
                    .setValue(groupEntity.getId().toString())
                    .build();
            groupsForMapping.add(multiValue);
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
