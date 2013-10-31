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
import java.util.UUID;

import javax.inject.Inject;

import org.osiam.resources.exceptions.ResourceExistsException;
import org.osiam.resources.helper.ScimConverter;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.resources.scim.User;
import org.osiam.storage.dao.GenericDAO;
import org.osiam.storage.dao.UserDAO;
import org.osiam.storage.entities.UserEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SCIMUserProvisioningBean extends SCIMProvisiongSkeleton<User> implements SCIMUserProvisioning {

    @Inject
    private ScimConverter scimConverter;

    @Inject
    private UserDAO userDao;

    @Override
    protected GenericDAO getDao() {
        return userDao;
    }

    @Override
    public User create(User user) {
        UserEntity userEntity = scimConverter.createFromScim(user);
        userEntity.setId(UUID.randomUUID());
        try {
            userDao.create(userEntity);
        } catch (Exception e) {
            if(e.getMessage().contains("scim_id_externalid_key")) {
                throw new ResourceExistsException("The user with the externalId " +
                        user.getExternalId() + " already exists.", e);
            }
            throw new ResourceExistsException("The user with name " +
                    user.getUserName() + " already exists.", e);
        }
        return userEntity.toScim();
    }

    @Override
    public User replace(String id, User user) {
        UserEntity userEntity = scimConverter.createFromScim(user);
        return userDao.update(userEntity).toScim();
    }

    @Override
    public SCIMSearchResult<User> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        List<User> users = new ArrayList<>();
        SCIMSearchResult<UserEntity> result = getDao().search(filter, sortBy, sortOrder, count, startIndex);
        for (Object g : result.getResources()) {
            users.add(User.Builder.generateForOutput(((UserEntity) g).toScim()));
        }
        return new SCIMSearchResult(users, result.getTotalResults(), count, result.getStartIndex(), result.getSchemas());
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


