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
import org.osiam.resources.scim.Constants;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.GroupEntity_;
import org.osiam.storage.filter.FilterParser;
import org.osiam.storage.filter.GroupFilterParser;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.metamodel.SingularAttribute;
import java.util.Set;
import java.util.logging.Level;


@Repository
@Transactional
public class GroupDao extends ResourceDao<GroupEntity> implements GenericDao<GroupEntity> {

    @Inject
    private GroupFilterParser filterParser;

    @Override
    public void create(GroupEntity group) {
        em.persist(group);
    }

    @Override
    public GroupEntity getById(String id) {
        try {
            return getInternalIdSkeleton(id);
        } catch (ClassCastException c) {
            LOGGER.log(Level.WARNING, c.getMessage(), c);
            throw new ResourceNotFoundException("Resource " + id + " is not a Group.", c);
        }
    }

    public void delete(String id) {
        GroupEntity groupEntity = getById(id);
        Set<GroupEntity> groups = groupEntity.getGroups();
        for (GroupEntity group : groups) {
            group.removeMember(groupEntity);
        }
        em.remove(groupEntity);
    }

    public GroupEntity update(GroupEntity entity) {
        return em.merge(entity);
    }

    @Override
    public SearchResult<GroupEntity> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        return search(GroupEntity.class, filter, count, startIndex, sortBy, sortOrder);
    }

    @Override
    protected FilterParser<GroupEntity> getFilterParser() {
        return filterParser;
    }

    @Override
    protected String getCoreSchema() {
        return Constants.GROUP_CORE_SCHEMA;
    }

    @Override
    protected Class<GroupEntity> getResourceClass() {
        return GroupEntity.class;
    }

    @Override
    protected SingularAttribute<? super GroupEntity, ?> getDefaultSortByField() {
        return GroupEntity_.displayName;
    }
}