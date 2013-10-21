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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.sql.JoinType;
import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.storage.entities.UserEntity;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.Query;
import java.util.logging.Level;

@Repository
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserDAO extends GetInternalIdSkeleton implements GenericDAO<UserEntity> {

    private static final int PW_LENGHT = 128;

    @Inject
    private PasswordEncoder passwordEncoder;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void create(UserEntity userEntity) {
        String hash = passwordEncoder.encodePassword(userEntity.getPassword(), userEntity.getId());
        userEntity.setPassword(hash);
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
        //not hashed ...
        String password = entity.getPassword();
        if (password.length() != PW_LENGHT) {
            entity.setPassword(passwordEncoder.encodePassword(password, entity.getId()));
        }
        em.merge(entity);
        return entity;
    }

    @Override
    public void delete(String id) {
        UserEntity userEntity = getById(id);
        em.remove(userEntity);

    }

    @Override
    public SCIMSearchResult<UserEntity> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        return search(UserEntity.class, filter, count, startIndex, sortBy, sortOrder);
    }

    @Override
    protected void createAliasesForCriteria(DetachedCriteria criteria) {
        criteria.createAlias("meta", "meta", JoinType.INNER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("name", "name", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("emails", "emails", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("phoneNumbers", "phoneNumbers", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("ims", "ims", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("photos", "photos", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("addresses", "addresses", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("groups", "groups", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("entitlements", "entitlements", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("roles", "roles", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
        criteria.createAlias("x509Certificates", "x509Certificates", JoinType.LEFT_OUTER_JOIN); // NOSONAR - no code duplication, need to set alias for types
    }
}