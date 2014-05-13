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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.osiam.resources.converter.GroupConverter;
import org.osiam.resources.exceptions.ResourceExistsException;
import org.osiam.resources.exceptions.ResourceNotFoundException;
import org.osiam.resources.provisioning.update.GroupUpdater;
import org.osiam.resources.scim.Constants;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.storage.dao.GroupDao;
import org.osiam.storage.dao.SearchResult;
import org.osiam.storage.entities.GroupEntity;
import org.osiam.storage.parser.LogicalOperatorRulesLexer;
import org.osiam.storage.parser.LogicalOperatorRulesParser;
import org.osiam.storage.query.OsiamAntlrErrorListener;
import org.osiam.storage.query.QueryFilterParser;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

@Service
public class SCIMGroupProvisioning implements SCIMProvisioning<Group> {

    private static final Logger LOGGER = Logger.getLogger(SCIMGroupProvisioning.class.getName());

    @Inject
    private GroupConverter groupConverter;

    @Inject
    private GroupDao groupDao;

    @Inject
    private GroupUpdater groupUpdater;
    
    @Inject
    private QueryFilterParser queryFilterParser;

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
        List<Group> groups = new ArrayList<>();

        ParseTree filterTree = queryFilterParser.getParseTree(filter);

        // Decrease startIndex by 1 because scim pagination starts at 1 and JPA doesn't
        SearchResult<GroupEntity> result = groupDao.search(filterTree, sortBy, sortOrder, count, startIndex - 1);

        for (GroupEntity group : result.results) {
            groups.add(groupConverter.toScim(group));
        }

        return new SCIMSearchResult<>(groups, result.totalResults, count, startIndex, Constants.GROUP_CORE_SCHEMA);
    }

    @Override
    public Group getById(String id) {
        try {
            return groupConverter.toScim(groupDao.getById(id));
        } catch (NoResultException nre) {
            LOGGER.log(Level.INFO, nre.getMessage(), nre);

            throw new ResourceNotFoundException(String.format("Group with id '%s' not found", id), nre);
        } catch (PersistenceException pe) {
            LOGGER.log(Level.WARNING, pe.getMessage(), pe);

            throw new ResourceNotFoundException(String.format("Group with id '%s' not found", id), pe);
        }
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

        Group result = groupConverter.toScim(groupEntity);

        return result;
    }

    @Override
    public void delete(String id) {
        try {
            groupDao.delete(id);
        } catch (NoResultException nre) {
            throw new ResourceNotFoundException(String.format("Group with id '%s' not found", id), nre);
        }
    }

}