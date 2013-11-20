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

import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.resources.helper.FilterParser;
import org.osiam.resources.helper.UserFilterParser;
import org.osiam.resources.scim.Constants;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.Set;
import java.util.logging.Level;

@Repository
@Transactional
public class UserDao extends ResourceDao<UserEntity> implements GenericDao<UserEntity> {

    @Inject
    private UserFilterParser filterParser;

    @Override
    public void create(UserEntity userEntity) {
        em.persist(userEntity);
    }

    @Override
    public UserEntity getById(String id) {
        try {
            return getInternalIdSkeleton(id);
        } catch (ClassCastException c) {
            LOGGER.log(Level.WARNING, c.getMessage(), c);
            throw new ResourceNotFoundException("Resource " + id + " is not an User.", c);
        }
    }

    public UserEntity getByUsername(String userName) {
        Query query = em.createNamedQuery("getUserByUsername");
        query.setParameter("username", userName);
        return getSingleInternalIdSkeleton(query, userName);
    }

    @Override
    public UserEntity update(UserEntity entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(String id) {
        UserEntity userEntity = getById(id);
        Set<GroupEntity> groups = userEntity.getGroups();
        for (GroupEntity group : groups) {
            group.removeMember(userEntity);
        }
        em.remove(userEntity);
    }

    @Override
    public SCIMSearchResult<UserEntity> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        return search(UserEntity.class, filter, count, startIndex, sortBy, sortOrder);
    }

    @Override
    protected FilterParser<UserEntity> getFilterParser() {
        return filterParser;
    }

    @Override
    protected Class<UserEntity> getResourceClass() {
        return UserEntity.class;
    }

    @Override
    protected String getCoreSchema() {
        return Constants.USER_CORE_SCHEMA;
    }

}