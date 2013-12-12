package org.osiam.resources.provisioning.update

import org.osiam.resources.scim.Group
import org.osiam.resources.scim.Meta
import org.osiam.storage.entities.GroupEntity

import spock.lang.Specification

class ResourceUpdaterSpec extends Specification {

    ResourceUpdater resourceUpdater = new ResourceUpdater()

    Meta metaWithAttributesToDelete = new Meta(attributes: ['externalId'] as Set)
    // resources are abstract so we use groups here with no loss of generality
    Group group
    GroupEntity groupEntity = Mock()

    def 'removing externalId works'() {
        given:
        group = new Group.Builder(meta: metaWithAttributesToDelete).build()

        when:
        resourceUpdater.update(group, groupEntity)

        then:
        1 * groupEntity.setExternalId(null)
    }

    def 'updating externalId works'() {
        given:
        group = new Group.Builder(externalId: 'irrelevant').build()

        when:
        resourceUpdater.update(group, groupEntity)

        then:
        1 * groupEntity.setExternalId('irrelevant')
    }

}
