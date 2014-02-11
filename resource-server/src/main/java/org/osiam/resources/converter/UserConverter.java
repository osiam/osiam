/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.GroupRef;
import org.osiam.resources.scim.User;
import org.osiam.storage.entities.AddressEntity;
import org.osiam.storage.entities.EmailEntity;
import org.osiam.storage.entities.EntitlementEntity;
import org.osiam.storage.entities.ExtensionFieldValueEntity;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.ImEntity;
import org.osiam.storage.entities.PhoneNumberEntity;
import org.osiam.storage.entities.PhotoEntity;
import org.osiam.storage.entities.RoleEntity;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.X509CertificateEntity;
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

    @Override
    public UserEntity fromScim(User user) {
        if (user == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            userEntity.setPassword(user.getPassword());
        }

        userEntity.setActive(user.isActive());
        userEntity.setDisplayName(user.getDisplayName());
        userEntity.setNickName(user.getNickName());
        userEntity.setExternalId(user.getExternalId() == null ? null : user.getExternalId().isEmpty() ? null : user
                .getExternalId()); // Due to uniqueness in databases
        userEntity.setPreferredLanguage(user.getPreferredLanguage());
        userEntity.setLocale(user.getLocale());
        userEntity.setProfileUrl(user.getProfileUrl());
        userEntity.setTimezone(user.getTimezone());
        userEntity.setTitle(user.getTitle());
        userEntity.setUserName(user.getUserName());
        userEntity.setUserType(user.getUserType());

        userEntity.setName(nameConverter.fromScim(user.getName()));

        Set<AddressEntity> addresses = convertMultiValueFromScim(addressConverter, new HashSet<>(user.getAddresses()));
        for (AddressEntity addressEntity : addresses) {
            userEntity.addAddress(addressEntity);
        }
        Set<EmailEntity> emails = convertMultiValueFromScim(emailConverter, new HashSet<>(user.getEmails()));
        for (EmailEntity emailEntitty : emails) {
            userEntity.addEmail(emailEntitty);
        }
        Set<EntitlementEntity> entitlements = convertMultiValueFromScim(entitlementConverter,
                new HashSet<>(user.getEntitlements()));
        for (EntitlementEntity entitlementEntitty : entitlements) {
            userEntity.addEntitlement(entitlementEntitty);
        }
        Set<ImEntity> ims = convertMultiValueFromScim(imConverter, new HashSet<>(user.getIms()));
        for (ImEntity imEntity : ims) {
            userEntity.addIm(imEntity);
        }
        Set<PhoneNumberEntity> phoneNumbers = convertMultiValueFromScim(phoneNumberConverter,
                new HashSet<>(user.getPhoneNumbers()));
        for (PhoneNumberEntity phoneNumberEntity : phoneNumbers) {
            userEntity.addPhoneNumber(phoneNumberEntity);
        }
        Set<PhotoEntity> photos = convertMultiValueFromScim(photoConverter, new HashSet<>(user.getPhotos()));
        for (PhotoEntity photoEntity : photos) {
            userEntity.addPhoto(photoEntity);
        }
        Set<RoleEntity> roles = convertMultiValueFromScim(roleConverter, new HashSet<>(user.getRoles()));
        for (RoleEntity roleEntity : roles) {
            userEntity.addRole(roleEntity);
        }
        Set<X509CertificateEntity> x509Certificates = convertMultiValueFromScim(x509CertificateConverter,
                new HashSet<>(user.getX509Certificates()));
        for (X509CertificateEntity x509CertificateEntity : x509Certificates) {
            userEntity.addX509Certificate(x509CertificateEntity);
        }
        Set<ExtensionFieldValueEntity> fieldValues = extensionConverter.fromScim(new HashSet<>(user.getAllExtensions()
                .values()));
        for (ExtensionFieldValueEntity fieldValue : fieldValues) {
            userEntity.addOrUpdateExtensionValue(fieldValue);
        }

        return userEntity;
    }

    @Override
    public User toScim(UserEntity entity) {
        if (entity == null) {
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
                .setTimezone(entity.getTimezone()).setTitle(entity.getTitle())
                .setUserType(entity.getUserType())
                .setExternalId(entity.getExternalId()).setId(entity.getId().toString())
                .setMeta(metaConverter.toScim(entity.getMeta()))
                .setAddresses(convertMultiValueToScim(addressConverter, entity.getAddresses()))
                .setEmails(convertMultiValueToScim(emailConverter, entity.getEmails()))
                .setEntitlements(convertMultiValueToScim(entitlementConverter, entity.getEntitlements()))
                .setGroups(entityGroupsToScim(entity.getGroups()))
                .setIms(convertMultiValueToScim(imConverter, entity.getIms()))
                .setPhoneNumbers(convertMultiValueToScim(phoneNumberConverter, entity.getPhoneNumbers()))
                .setPhotos(convertMultiValueToScim(photoConverter, entity.getPhotos()))
                .setRoles(convertMultiValueToScim(roleConverter, entity.getRoles()))
                .setX509Certificates(convertMultiValueToScim(x509CertificateConverter, entity.getX509Certificates()));

        addExtensions(userBuilder, entity.getExtensionFieldValues());

        return userBuilder.build();

    }

    private void addExtensions(User.Builder userBuilder, Set<ExtensionFieldValueEntity> extensionFieldValues) {

        Set<Extension> extensions = extensionConverter.toScim(extensionFieldValues);

        for (Extension extension : extensions) {
            userBuilder.addExtension(extension);
        }
    }

    private <S, E> Set<E> convertMultiValueFromScim(Converter<S, E> converter, Set<S> multiValues) {
        Set<E> entities = new HashSet<>();

        for (S multiValue : multiValues) {
            E entity = converter.fromScim(multiValue);
            entities.add(entity);
        }
        return entities;
    }

    private <S, E> List<S> convertMultiValueToScim(Converter<S, E> converter, Set<E> entities) {
        List<S> multiValues = new ArrayList<S>();

        for (E entity : entities) {
            S multitValue = converter.toScim(entity);
            multiValues.add(multitValue);
        }
        return multiValues;
    }

    private List<GroupRef> entityGroupsToScim(Set<GroupEntity> groupEntities) {
        List<GroupRef> groupsForMapping = new ArrayList<>();

        if (groupEntities != null) {
            for (GroupEntity groupEntity : groupEntities) {
                GroupRef groupRef = new GroupRef.Builder()
                        .setDisplay(groupEntity.getDisplayName()).setValue(groupEntity.getId().toString()).build();
                groupsForMapping.add(groupRef);
            }
        }

        return groupsForMapping;
    }

}
