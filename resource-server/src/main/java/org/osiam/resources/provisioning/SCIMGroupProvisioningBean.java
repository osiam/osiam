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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.osiam.resources.converter.Converter;
import org.osiam.resources.converter.GroupConverter;
import org.osiam.resources.exceptions.ResourceExistsException;
import org.osiam.resources.scim.Constants;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.storage.dao.GenericDao;
import org.osiam.storage.dao.GroupDao;
import org.osiam.storage.dao.SearchResult;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.entities.InternalIdSkeleton;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class SCIMGroupProvisioningBean extends SCIMProvisiongSkeleton<Group, GroupEntity> implements
        SCIMGroupProvisioning {

    private static final Logger LOGGER = Logger.getLogger(SCIMGroupProvisioningBean.class.getName());

    @Inject
    private GroupConverter groupConverter;

    @Inject
    private GroupDao groupDao;

    @Override
    protected GenericDao<GroupEntity> getDao() {
        return groupDao;
    }

    @Override
    protected Converter<Group, GroupEntity> getConverter() {
        return groupConverter;
    }

    @Override
    public Group create(Group group) {
        GroupEntity enrichedGroup = groupConverter.fromScim(group);
        enrichedGroup.setId(UUID.randomUUID());
        try {
            groupDao.create(enrichedGroup);
        } catch (DataIntegrityViolationException e) {
            LOGGER.log(Level.INFO, "An exception got thrown while creating a group.", e);

            throw new ResourceExistsException(group.getDisplayName() + " already exists.", e);
        }
        return groupConverter.toScim(enrichedGroup);
    }

    @Override
    public Group replace(String id, Group group) {

        GroupEntity existingEntity = groupDao.getById(id);

        GroupEntity groupEntity = groupConverter.fromScim(group);

        groupEntity.setInternalId(existingEntity.getInternalId());
        groupEntity.setId(existingEntity.getId());
        groupEntity.setMeta(existingEntity.getMeta());
        groupEntity.touch();

        groupEntity = groupDao.update(groupEntity);
        return groupConverter.toScim(groupEntity);
    }

    @Override
    public Group update(String id, Group group) {
        //TODO: Refactor GenericScimToEntityWrapper to support membership updating without this ugly converting stuff
        Group readyForUpdate = super.update(id, group);
        GroupEntity groupForUpdate = groupConverter.fromScim(readyForUpdate);

        GroupEntity existingEntity = groupDao.getById(id);

        groupForUpdate.setId(existingEntity.getId());
        groupForUpdate.setInternalId(existingEntity.getInternalId());
        groupForUpdate.setMeta(existingEntity.getMeta());
        groupForUpdate.touch();

        GroupEntity updatedGroup = groupDao.update(groupForUpdate);
        return groupConverter.toScim(updatedGroup);
    }

    @Override
    public SCIMSearchResult<Group> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        List<Group> groups = new ArrayList<>();

        // Decrease startIndex by 1 because scim pagination starts at 1 and JPA doesn't
        SearchResult<GroupEntity> result = getDao().search(filter, sortBy, sortOrder, count, startIndex - 1);

        for (GroupEntity group : result.results) {
            groups.add(groupConverter.toScim(group));
        }

        return new SCIMSearchResult<>(groups, result.totalResults, count, startIndex, Constants.GROUP_CORE_SCHEMA);
    }

    @Override
    protected SCIMEntities getScimEntities() {
        return GroupSCIMEntities.ENTITIES;
    }

    @Override
    public GenericSCIMToEntityWrapper.For getTarget() {
        return GenericSCIMToEntityWrapper.For.GROUP;
    }

}