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
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.GroupEntity_;
import org.osiam.storage.query.GroupFilterParser;
import org.springframework.stereotype.Repository;

@Repository
public class GroupDao implements GenericDao<GroupEntity> {

    @Inject
    private GroupFilterParser filterParser;

    @Inject
    private ResourceDao resourceDao;

    @Override
    public void create(GroupEntity group) {
        resourceDao.create(group);
    }

    @Override
    public GroupEntity getById(String id) {
        try {
            return resourceDao.getById(id, GroupEntity.class, GroupEntity.ENTITYGRAPH_ATTRIBUTES);
        } catch (ResourceNotFoundException rnfe) {
            throw new ResourceNotFoundException(String.format("Group with id '%s' not found", id), rnfe);
        }
    }

    /**
     * Checks if a displayName is already taken by another group.
     *
     * @param displayName
     *            the displayName to check
     * @return true if the displayName is taken, otherwise false
     */
    public boolean isDisplayNameAlreadyTaken(String displayName) {
        return isDisplayNameAlreadyTaken(displayName, null);
    }

    /**
     * Checks if a displayName is already taken by another group. Ignores the group with the given id.
     *
     * @param displayName
     *            the displayName to check
     * @param id
     *            the id of the group to ignore
     * @return true if the displayName is taken, otherwise false
     */
    public boolean isDisplayNameAlreadyTaken(String displayName, String id) {
        return resourceDao.isUniqueAttributeAlreadyTaken(displayName, id, GroupEntity_.displayName, GroupEntity.class);
    }
    
    /**
     * Checks if a external id is already taken by another group or user.
     *
     * @param externalId
     *            the external id to check
     * @return true if the external id is taken, otherwise false
     */
    public boolean isExternalIdAlreadyTaken(String externalId) {
        return resourceDao.isExternalIdAlreadyTaken(externalId);
    }
    
    /**
     * Checks if a external id is already taken by another group or user. Ignores the group with the given id.
     *
     * @param externalId
     *            the external id to check
     * @param id
     *            the id of the group to ignore
     * @return true if the displayName is taken, otherwise false
     */
    public boolean isExternalIdAlreadyTaken(String externalId, String id) {
        return resourceDao.isExternalIdAlreadyTaken(externalId, id);
    }
    

    @Override
    public void delete(String id) {
        try {
            resourceDao.delete(id);
        } catch (ResourceNotFoundException rnfe) {
            throw new ResourceNotFoundException(String.format("Group with id '%s' not found", id), rnfe);
        }
    }

    @Override
    public GroupEntity update(GroupEntity entity) {
        return resourceDao.update(entity);
    }

    @Override
    public SearchResult<GroupEntity> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        return resourceDao.search(GroupEntity.class, filter, count, startIndex, sortBy, sortOrder, filterParser);
    }

}