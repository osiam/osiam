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

import org.osiam.resources.exceptions.ResourceExistsException;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.osiam.storage.dao.GenericDAO;
import org.osiam.storage.dao.GroupDAO;
import org.osiam.storage.entities.GroupEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class SCIMGroupProvisioningBean extends SCIMProvisiongSkeleton<Group> implements SCIMGroupProvisioning {

    private static final Logger LOGGER = Logger.getLogger(SCIMGroupProvisioningBean.class.getName());

    @Inject
    private GroupDAO groupDAO;

    @Override
    protected GenericDAO getDao() {
        return groupDAO;
    }

    @Override
    public Group create(Group group) {
        GroupEntity enrichedGroup = GroupEntity.fromScim(group);
        enrichedGroup.setId(UUID.randomUUID());
        try {
            groupDAO.create(enrichedGroup);
        } catch (DataIntegrityViolationException e) {
            LOGGER.log(Level.INFO, "An exception got thrown while creating a group.", e);

            throw new ResourceExistsException(group.getDisplayName() + " already exists.", e);
        }
        return enrichedGroup.toScim();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SCIMSearchResult<Group> search(String filter, String sortBy, String sortOrder, int count, int startIndex) {
        List<Group> groups = new ArrayList<>();
        SCIMSearchResult<GroupEntity> result = getDao().search(filter, sortBy, sortOrder, count, startIndex);
        for (Object g : result.getResources()) {
            groups.add(((GroupEntity) g).toScim());
        }
        return new SCIMSearchResult(groups, result.getTotalResults(), count, result.getStartIndex(), result.getSchemas());
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