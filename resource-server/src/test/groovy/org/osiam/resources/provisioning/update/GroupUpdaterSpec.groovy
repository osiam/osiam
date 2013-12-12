package org.osiam.resources.provisioning.update

import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MemberRef
import org.osiam.resources.scim.Meta
import org.osiam.storage.dao.ResourceDao
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.ResourceEntity

import spock.lang.Specification
import spock.lang.Unroll

class GroupUpdaterSpec extends Specification {

    ResourceUpdater resourceUpdater = Mock()
    ResourceDao resourceDao = Mock()
    GroupUpdater groupUpdater = new GroupUpdater(resourceUpdater: resourceUpdater, resourceDao: resourceDao)

    Group group
    GroupEntity groupEntity = Mock()

    def 'updating a group triggers resource updater'() {
        given:
        group = new Group.Builder().build()

        when:
        groupUpdater.update(group, groupEntity)

        then:
        1 * resourceUpdater.update(group, groupEntity)
    }

    def 'removing displayName is not possible'() {
        given:
        Meta meta = new Meta(attributes: ['displayName'] as Set)
        group = new Group.Builder(meta: meta).build()

        when:
        groupUpdater.update(group, groupEntity)

        then:
        0 * groupEntity.setDisplayName(_)
    }

    def 'updating displayName works'() {
        given:
        def displayName = 'irrelevant'
        group = new Group.Builder(displayName: displayName).build()

        when:
        groupUpdater.update(group, groupEntity)

        then:
        1 * groupEntity.setDisplayName(displayName)
    }

    @Unroll
    def 'updating displayName to #value is not possible'() {
        given:
        group = new Group.Builder(displayName: displayName).build()

        when:
        groupUpdater.update(group, groupEntity)

        then:
        0 * groupEntity.setDisplayName(displayName)

        where:
        value          | displayName
        'null'         | null
        'empty string' | ''
    }

    def 'removing all members works'() {
        given:
        Meta meta = new Meta(attributes: ['members'] as Set)
        group = new Group.Builder(meta: meta).build()

        when:
        groupUpdater.update(group, groupEntity)

        then:
        1 * groupEntity.removeAllMembers()
    }

    def 'adding a member works'(){
        given:
        def memberId = 'irrelevant'
        def member = new GroupEntity()

        MemberRef memberRef = new MemberRef.Builder(value: memberId).build()
        group = new Group.Builder(members: [memberRef] as Set).build()

        when:
        groupUpdater.update(group, groupEntity)

        then:
        1 * resourceDao.getById(memberId, ResourceEntity) >> member
        1 * groupEntity.addMember(member)
    }

    def 'removing a member works'(){
        given:
        def memberId = 'irrelevant'
        def member = new GroupEntity()

        MemberRef memberRef = new MemberRef.Builder(value: memberId, operation: 'delete').build()
        group = new Group.Builder(members: [memberRef] as Set).build()

        when:
        groupUpdater.update(group, groupEntity)

        then:
        1 * resourceDao.getById(memberId, ResourceEntity) >> member
        1 * groupEntity.removeMember(member)
    }
}
