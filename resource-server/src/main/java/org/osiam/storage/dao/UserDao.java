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

package org.osiam.storage.dao;

import javax.inject.Inject;

import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.storage.entities.UserEntity;
import org.osiam.storage.entities.UserEntity_;
import org.osiam.storage.query.UserFilterParser;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao implements GenericDao<UserEntity> {

    @Inject
    private UserFilterParser filterParser;

    @Inject
    private ResourceDao resourceDao;

    @Override
    public void create(UserEntity userEntity) {
        resourceDao.create(userEntity);
    }

    @Override
    public UserEntity getById(String id) {
        try {
            return resourceDao.getById(id, UserEntity.class);
        } catch (ResourceNotFoundException rnfe) {
            throw new ResourceNotFoundException(String.format("User with id '%s' not found", id), rnfe);
        }
    }

    public UserEntity getByUsername(String userName) {
        try {
            return resourceDao.getByAttribute(UserEntity_.userName, userName, UserEntity.class);
        } catch (ResourceNotFoundException rnfe) {
            throw new ResourceNotFoundException(String.format("User with userName '%s' not found", userName), rnfe);
        }
    }

    @Override
    public UserEntity update(UserEntity entity) {
        return resourceDao.update(entity);
    }

    @Override
    public void delete(String id) {
        try {
            resourceDao.delete(id);
        } catch (ResourceNotFoundException rnfe) {
            throw new ResourceNotFoundException(String.format("User with id '%s' not found", id), rnfe);
        }
    }

    @Override
    public SearchResult<UserEntity> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        return resourceDao.search(UserEntity.class, filter, count, startIndex, sortBy, sortOrder, filterParser);
    }

}