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

package org.osiam.resources.provisioning.update;

import org.osiam.resources.exception.ResourceExistsException;
import org.osiam.resources.scim.Resource;
import org.osiam.storage.dao.ResourceDao;
import org.osiam.storage.entities.ResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The ResourceUpdater provides the functionality to update the {@link ResourceEntity} of a UserEntity
 */
@Service
public class ResourceUpdater {

    private ResourceDao resourceDao;

    @Autowired
    public ResourceUpdater(ResourceDao resourceDao) {
        this.resourceDao = resourceDao;
    }

    /**
     * updates (adds new, delete, updates) the given {@link ResourceEntity} based on the given {@link Resource}
     *
     * @param resource       {@link Resource} to be deleted, updated or added
     * @param resourceEntity {@link ResourceEntity} which will be updated
     */
    public void update(Resource resource, ResourceEntity resourceEntity) {

        if (resource.getMeta() != null && resource.getMeta().getAttributes() != null) {
            for (String attribute : resource.getMeta().getAttributes()) {
                if (attribute.equalsIgnoreCase("externalId")) {
                    resourceEntity.setExternalId(null);
                }
            }
        }

        String externalId = resource.getExternalId();

        if (externalId != null && !externalId.isEmpty()) {
            if (resourceDao.isExternalIdAlreadyTaken(externalId, resourceEntity.getId().toString())) {
                throw new ResourceExistsException(String.format("Resource with externalId '%s' already exists",
                        externalId));
            }

            resourceEntity.setExternalId(externalId);
        }
    }
}
