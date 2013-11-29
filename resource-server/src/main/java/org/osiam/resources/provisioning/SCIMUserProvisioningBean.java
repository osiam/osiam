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

package org.osiam.resources.provisioning;

import org.osiam.resources.converter.Converter;
import org.osiam.resources.converter.UserConverter;
import org.osiam.resources.exceptions.ResourceExistsException;
import org.osiam.resources.scim.*;
import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.dao.GenericDao;
import org.osiam.storage.dao.SearchResult;
import org.osiam.storage.dao.UserDao;
import org.osiam.storage.entities.ExtensionEntity;
import org.osiam.storage.entities.ExtensionFieldEntity;
import org.osiam.storage.entities.ExtensionFieldValueEntity;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.helper.NumberPadder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

@Service
@Transactional
public class SCIMUserProvisioningBean extends SCIMProvisiongSkeleton<User, UserEntity> implements SCIMUserProvisioning {

    @Inject
    private UserConverter userConverter;

    @Inject
    private UserDao userDao;

    @Inject
    private ExtensionDao extensionDao;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private NumberPadder numberPadder;

    @Override
    protected GenericDao<UserEntity> getDao() {
        return userDao;
    }

    @Override
    protected Converter<User, UserEntity> getConverter() {
        return userConverter;
    }

    @Override
    public User create(User user) {
        UserEntity userEntity = userConverter.fromScim(user);
        userEntity.setId(UUID.randomUUID());

        String hashedPassword = passwordEncoder.encodePassword(user.getPassword(), userEntity.getId());
        userEntity.setPassword(hashedPassword);

        try {
            userDao.create(userEntity);
        } catch (Exception e) {
            if (e.getMessage().contains("scim_id_externalid_key")) {
                throw new ResourceExistsException("The user with the externalId " +
                        user.getExternalId() + " already exists.", e);
            }
            throw new ResourceExistsException("The user with name " +
                    user.getUserName() + " already exists.", e);
        }
        return userConverter.toScim(userEntity);
    }

    @Override
    public User replace(String id, User user) {

        UserEntity existingEntity = userDao.getById(id);

        UserEntity userEntity = userConverter.fromScim(user);

        userEntity.setInternalId(existingEntity.getInternalId());
        userEntity.setMeta(existingEntity.getMeta());
        userEntity.setId(existingEntity.getId());

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encodePassword(user.getPassword(), userEntity.getId());
            userEntity.setPassword(hashedPassword);
        } else {
            userEntity.setPassword(existingEntity.getPassword());
        }

        userEntity.touch();
        return userConverter.toScim(userDao.update(userEntity));
    }

    @Override
    public SCIMSearchResult<User> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        List<User> users = new ArrayList<>();

        // Decrease startIndex by 1 because scim pagination starts at 1 and JPA doesn't
        SearchResult<UserEntity> result = getDao().search(filter, sortBy, sortOrder, count, startIndex - 1);

        for (UserEntity userEntity : result.results) {
            User scimResultUser = userConverter.toScim(userEntity);
            users.add(User.Builder.generateForOutput(scimResultUser));
        }

        return new SCIMSearchResult<>(users, result.totalResults, count, startIndex, Constants.USER_CORE_SCHEMA);
    }

    @Override
    public User update(String id, User user) {

        super.update(id, user);

        UserEntity userEntity = userDao.getById(id);

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encodePassword(user.getPassword(), userEntity.getId());
            userEntity.setPassword(hashedPassword);
        }

        if (user.getAllExtensions().size() != 0) {
            for (Entry<String, Extension> extensionEntry : user.getAllExtensions().entrySet()) {
                updateExtension(extensionEntry, userEntity);
            }
        }

        userEntity.touch();
        return userConverter.toScim(userEntity);
    }

    private void updateExtension(Entry<String, Extension> extensionEntry, UserEntity userEntity) {
        String urn = extensionEntry.getKey();
        Extension updatedExtension = extensionEntry.getValue();
        ExtensionEntity extensionEntity = extensionDao.getExtensionByUrn(urn);

        for (ExtensionFieldEntity extensionField : extensionEntity.getFields()) {
            String fieldName = extensionField.getName();
            ExtensionFieldValueEntity extensionFieldValue = findExtensionFieldValue(extensionField, userEntity);

            boolean isFieldPresent = updatedExtension.isFieldPresent(fieldName);
            if (extensionFieldValue == null && !isFieldPresent) {
                continue;
            } else if (extensionFieldValue == null && isFieldPresent) {
                extensionFieldValue = new ExtensionFieldValueEntity();
            } else if (extensionFieldValue != null && !isFieldPresent) {
                continue;
            }

            String newValue = getNewExtensionValue(extensionField, updatedExtension, fieldName);
            if (newValue == null) {
                continue;
            }

            extensionFieldValue.setValue(newValue);
            extensionFieldValue.setExtensionField(extensionField);
            userEntity.addOrUpdateExtensionValue(extensionFieldValue);
        }
    }

    private String getNewExtensionValue(ExtensionFieldEntity extensionField, Extension updatedExtension,
            String fieldName) {
        String newValue = updatedExtension.getField(fieldName, ExtensionFieldType.STRING);
        if (newValue != null &&
                (extensionField.getType() == ExtensionFieldType.INTEGER
                || extensionField.getType() == ExtensionFieldType.DECIMAL)) {
            newValue = numberPadder.pad(newValue);
        }
        return newValue;
    }

    private ExtensionFieldValueEntity findExtensionFieldValue(ExtensionFieldEntity extensionField, UserEntity userEntity) {
        for (ExtensionFieldValueEntity extensionFieldValue : userEntity.getUserExtensions()) {
            if (extensionFieldValue.getExtensionField().equals(extensionField)) {
                return extensionFieldValue;
            }
        }

        return null;
    }

    @Override
    protected SCIMEntities getScimEntities() {
        return UserSCIMEntities.ENTITIES;
    }

    @Override
    public GenericSCIMToEntityWrapper.For getTarget() {
        return GenericSCIMToEntityWrapper.For.USER;
    }

}
