/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.provisioning;

import org.antlr.v4.runtime.tree.ParseTree;
import org.osiam.resources.converter.GroupConverter;
import org.osiam.resources.exception.ResourceExistsException;
import org.osiam.resources.provisioning.update.GroupUpdater;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.storage.dao.GroupDao;
import org.osiam.storage.dao.SearchResult;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.query.QueryFilterParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SCIMGroupProvisioning implements SCIMProvisioning<Group> {

    private final GroupConverter groupConverter;
    private final GroupDao groupDao;
    private final GroupUpdater groupUpdater;

    @Autowired
    public SCIMGroupProvisioning(GroupConverter groupConverter, GroupDao groupDao, GroupUpdater groupUpdater) {
        this.groupConverter = groupConverter;
        this.groupDao = groupDao;
        this.groupUpdater = groupUpdater;
    }

    @Override
    public Group create(Group group) {
        if (groupDao.isDisplayNameAlreadyTaken(group.getDisplayName())) {
            throw new ResourceExistsException(String.format(
                    "Can't create a group. The displayname \"%s\" is already taken.", group.getDisplayName()));
        }
        if (groupDao.isExternalIdAlreadyTaken(group.getExternalId())) {
            throw new ResourceExistsException(String.format(
                    "Can't create a group. The externalId \"%s\" is already taken.", group.getExternalId()));
        }
        GroupEntity groupEntity = groupConverter.fromScim(group);
        groupEntity.setId(UUID.randomUUID());

        groupDao.create(groupEntity);

        return groupConverter.toScim(groupEntity);
    }

    @Override
    public Group replace(String id, Group group) {
        if (groupDao.isDisplayNameAlreadyTaken(group.getDisplayName(), id)) {
            throw new ResourceExistsException(String.format("Can't replace the group with the id \"" + id
                    + "\". The displayname \"%s\" is already taken.", group.getDisplayName()));
        }
        if (groupDao.isExternalIdAlreadyTaken(group.getExternalId(), id)) {
            throw new ResourceExistsException(String.format("Can't replace the group with the id \"" + id
                    + "\". The externalId \"%s\" is already taken.", group.getExternalId()));
        }
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
    public SCIMSearchResult<Group> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        QueryFilterParser queryFilterParser = new QueryFilterParser();
        List<Group> groups = new ArrayList<>();

        ParseTree filterTree = queryFilterParser.getParseTree(filter);

        // Decrease startIndex by 1 because scim pagination starts at 1 and JPA doesn't
        SearchResult<GroupEntity> result = groupDao.search(filterTree, sortBy, sortOrder, count, startIndex - 1);

        for (GroupEntity group : result.results) {
            groups.add(groupConverter.toScim(group));
        }

        return new SCIMSearchResult<>(groups, result.totalResults, count, startIndex);
    }

    @Override
    public Group getById(String id) {
        return groupConverter.toScim(groupDao.getById(id));
    }

    @Override
    public long count() {
        return groupDao.count();
    }

    @Override
    public Group update(String id, Group group) {
        if (groupDao.isDisplayNameAlreadyTaken(group.getDisplayName(), id)) {
            throw new ResourceExistsException(String.format("Can't update the group with the id \"" + id
                    + "\". The displayname \"%s\" is already taken.", group.getDisplayName()));
        }
        if (groupDao.isExternalIdAlreadyTaken(group.getExternalId(), id)) {
            throw new ResourceExistsException(String.format("Can't update the group with the id \"" + id
                    + "\". The externalId \"%s\" is already taken.", group.getExternalId()));
        }
        GroupEntity groupEntity = groupDao.getById(id);

        groupUpdater.update(group, groupEntity);

        groupEntity.touch();

        return groupConverter.toScim(groupEntity);
    }

    @Override
    public void delete(String id) {
        groupDao.delete(id);
    }
}
