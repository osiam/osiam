package org.osiam.resources.helper;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.osiam.resources.scim.Address;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.MultiValuedAttribute;
import org.osiam.resources.scim.Name;
import org.osiam.resources.scim.User;
import org.osiam.storage.entities.AddressEntity;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.EntitlementsEntity;
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.NameEntity;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.PhotoEntity;
import org.osiam.storage.entities.RolesEntity;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.X509CertificateEntity;
import org.osiam.storage.entities.extension.ExtensionEntity;
import org.osiam.storage.entities.extension.ExtensionFieldEntity;
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScimConverter {
    
    @PersistenceContext
    private EntityManager em;
    
    public User toScim(UserEntity userEntity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    public UserEntity fromScim(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setActive(user.isActive());
        userEntity.setAddresses(scimUserAddressesToEntity(user.getAddresses()));
        userEntity.setDisplayName(user.getDisplayName());
        userEntity.setEmails(scimEmailsToEntity(user.getEmails()));
        userEntity.setEntitlements(scimEntitlementsToEntity(user.getEntitlements()));
        userEntity.setExternalId(user.getExternalId() == null ? null : user.getExternalId().equals("") ? null : user.getExternalId()); //Due to uniqueness in databases
        userEntity.setIms(scimImsToEntity(user.getIms()));
        userEntity.setLocale(user.getLocale());
        userEntity.setName(scimNameToEntity(user.getName()));
        userEntity.setNickName(user.getNickName());
        userEntity.setPassword(user.getPassword());
        userEntity.setPhoneNumbers(scimPhonenumbersToEntity(user.getPhoneNumbers()));
        userEntity.setPhotos(scimPhotosToEntity(user.getPhotos()));
        userEntity.setPreferredLanguage(user.getPreferredLanguage());
        userEntity.setProfileUrl(user.getProfileUrl());
        userEntity.setRoles(scimUserRolesToEntity(user.getRoles()));
        userEntity.setTimezone(user.getTimezone());
        userEntity.setTitle(user.getTitle());
        userEntity.setUserName(user.getUserName());
        userEntity.setUserType(user.getUserType());
        userEntity.setX509Certificates(scimCertificatesToEntity(user.getX509Certificates()));
        userEntity.setUserExtensions(scimExtensionsToEntity(user, userEntity));
        
        
        
        return userEntity;
    }
    
    private Set<ExtensionFieldValueEntity> scimExtensionsToEntity(User scimUser, UserEntity userEntity) {
        Set<ExtensionFieldValueEntity> extensionFieldValueEntities = new HashSet<>();

        Set<String> userExtensionUris = scimUser.getAllExtensions().keySet();
        for (String urn : userExtensionUris) {
            Set<ExtensionFieldValueEntity> extensionFieldValues = mappingScimUserExtensionToEntity(scimUser.getExtension(urn), userEntity);
            addExtensionUrnToExtensionFields(extensionFieldValues, urn);
            extensionFieldValueEntities.addAll(extensionFieldValues);
        }
        return extensionFieldValueEntities;
    }

    private Set<ExtensionFieldValueEntity> mappingScimUserExtensionToEntity(Extension scimExtension, UserEntity userEntity) {
        ExtensionEntity extensionEntity = getExtensionFromDb(scimExtension.getUrn());
        Set<ExtensionFieldValueEntity> extensionFieldValueEntities = new HashSet<>();

        for (ExtensionFieldEntity extensionFieldEntity : extensionEntity.getExtensionFields()) {
            ExtensionFieldValueEntity extensionFieldValueEntity = new ExtensionFieldValueEntity();
            extensionFieldValueEntity.setExtensionField(extensionFieldEntity);
            extensionFieldValueEntity.setUser(userEntity);
            
            if(scimExtension.isFieldPresent(extensionFieldEntity.getName())) {
                String extensionFieldValue = scimExtension.getField(extensionFieldEntity.getName());
                extensionFieldValueEntity.setValue(extensionFieldValue);
            } else {
                extensionFieldValueEntity.setValue("");
            }
            
            extensionFieldValueEntities.add(extensionFieldValueEntity);
        }
        
        return extensionFieldValueEntities;
    }

    private ExtensionEntity getExtensionFromDb(String urn) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ExtensionEntity> cq = cb.createQuery(ExtensionEntity.class);
        Root<ExtensionEntity> extension = cq.from(ExtensionEntity.class);
        
        cq.select(extension).where(cb.equal(extension.get("urn"), urn));
        
        TypedQuery<ExtensionEntity> query = em.createQuery(cq);
        
        return query.getSingleResult();
    }

    private void addExtensionUrnToExtensionFields(Set<ExtensionFieldValueEntity> extensionFieldValueEntities, String urn) {
        Set<ExtensionFieldEntity>  extensionFieldEntitySet = new HashSet<>();

        for (ExtensionFieldValueEntity extensionFieldValueEntity :extensionFieldValueEntities) {
            extensionFieldEntitySet.add(extensionFieldValueEntity.getExtensionField());
        }

        ExtensionEntity extensionEntity = new ExtensionEntity();
        extensionEntity.setExtensionUrn(urn);
        extensionEntity.setExtensionFields(extensionFieldEntitySet);

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
