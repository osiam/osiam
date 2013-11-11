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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.inject.Inject;

import org.osiam.resources.converter.Converter;
import org.osiam.resources.converter.UserConverter;
import org.osiam.resources.exceptions.ResourceExistsException;
import org.osiam.resources.helper.ScimConverter;
import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;
import org.osiam.storage.dao.ExtensionDao;
import org.osiam.storage.dao.GenericDAO;
import org.osiam.storage.dao.UserDAO;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.extension.ExtensionEntity;
import org.osiam.storage.entities.extension.ExtensionFieldEntity;
import org.osiam.storage.entities.extension.ExtensionFieldValueEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SCIMUserProvisioningBean extends SCIMProvisiongSkeleton<User, UserEntity> implements SCIMUserProvisioning {

    @Inject
    private UserConverter userConverter;
    
    @Inject
    private ScimConverter scimConverter;

    @Inject
    private UserDAO userDao;

    @Inject
    private ExtensionDao extensionDao;

    @Override
    protected GenericDAO<UserEntity> getDao() {
        return userDao;
    }
    
    @Override
    protected Converter<User, UserEntity> getConverter() {
        return userConverter;
    }

    @Override
    public User create(User user) {
        UserEntity userEntity = scimConverter.createFromScim(user);
        userEntity.setId(UUID.randomUUID());
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
        UserEntity userEntity = scimConverter.createFromScim(user, id);
        userEntity.touch();
        return userConverter.toScim(userDao.update(userEntity));
    }

    @Override
    public SCIMSearchResult<User> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        List<User> users = new ArrayList<>();
        SCIMSearchResult<UserEntity> result = getDao().search(filter, sortBy, sortOrder, count, startIndex);
        for (Object g : result.getResources()) {
            User scimResultUser = new UserConverter().toScim((UserEntity) g);
            users.add(User.Builder.generateForOutput(scimResultUser));
        }
        return new SCIMSearchResult(users, result.getTotalResults(), count, result.getStartIndex(), result.getSchemas());
    }

    @Override
    public User update(String id, User user) {
        User updatedUser = super.update(id, user);

        if (user.getAllExtensions().size() == 0) {
            return updatedUser;
        }

        UserEntity userEntity = userDao.getById(id);

        for (Entry<String, Extension> extensionEntry : user.getAllExtensions().entrySet()) {
            updateExtension(extensionEntry, userEntity);
        }

        return userConverter.toScim(userEntity);
    }

    private void updateExtension(Entry<String, Extension> extensionEntry, UserEntity userEntity) {
        String urn = extensionEntry.getKey();
        Extension updatedExtension = extensionEntry.getValue();
        ExtensionEntity extensionEntity = extensionDao.getExtensionByUrn(urn);

        for (ExtensionFieldEntity extensionField : extensionEntity.getFields()) {
            String fieldName = extensionField.getName();
            ExtensionFieldValueEntity extensionFieldValue = findExtensionFieldValue(extensionField, userEntity);

            if(extensionFieldValue == null && !updatedExtension.isFieldPresent(fieldName)) {
                continue;
            } else if (extensionFieldValue == null && updatedExtension.isFieldPresent(fieldName)) {
                extensionFieldValue = new ExtensionFieldValueEntity();
            } else if (extensionFieldValue != null && !updatedExtension.isFieldPresent(fieldName)) {
                continue;
            }

            String newValue = updatedExtension.getField(fieldName, ExtensionFieldType.STRING);
            if(newValue == null) {
                continue;
            }

            extensionFieldValue.setValue(newValue);

            userEntity.addOrUpdateExtensionValue(extensionField, extensionFieldValue);
        }
    }

    private ExtensionFieldValueEntity findExtensionFieldValue(ExtensionFieldEntity extensionField, UserEntity userEntity) {
        for (ExtensionFieldValueEntity extensionFieldValue : userEntity.getUserExtensions()) {
            if(extensionFieldValue.getExtensionField().equals(extensionField)) {
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


