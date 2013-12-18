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

        then: 'the entity was not changed'
        0 * groupEntity._
    }

    def 'updating displayName is possible'() {
        given:
        def displayName = 'irrelevant'
        group = new Group.Builder(displayName: displayName).build()

        when:
        groupUpdater.update(group, groupEntity)

        then: 'only the displayName was changed'
        1 * groupEntity.setDisplayName(displayName)
        and: 'nothing else was changed'
        0 * groupEntity._
    }

    @Unroll
    def 'updating displayName to #value is not possible'() {
        given:
        group = new Group.Builder(displayName: displayName).build()

        when:
        groupUpdater.update(group, groupEntity)

        then: 'the entity was not changed'
        0 * groupEntity._

        where:
        value          | displayName
        'null'         | null
        'empty string' | ''
    }

    def 'removing all members is possible'() {
        given:
        Meta meta = new Meta(attributes: ['members'] as Set)
        group = new Group.Builder(meta: meta).build()

        when:
        groupUpdater.update(group, groupEntity)

        then: 'all members were removed'
        1 * groupEntity.removeAllMembers()
        and: 'nothing else was changed'
        0 * groupEntity._
    }

    def 'adding a member is possible'(){
        given:
        def memberId = 'irrelevant'
        def member = new GroupEntity()

        MemberRef memberRef = new MemberRef.Builder(value: memberId).build()
        group = new Group.Builder(members: [memberRef] as Set).build()

        when:
        groupUpdater.update(group, groupEntity)

        then: 'the member was retrieved via the ResourceDao'
        1 * resourceDao.getById(memberId, ResourceEntity) >> member
        and: 'the retrieved member was added to the group'
        1 * groupEntity.addMember(member)
        and: 'nothing else was changed'
        0 * groupEntity._
    }

    def 'removing a member is possible'(){
        given:
        def memberId = UUID.randomUUID()
        GroupEntity member = new GroupEntity(id: memberId)

        MemberRef memberRef = new MemberRef.Builder(value: memberId.toString(), operation: 'delete').build()
        group = new Group.Builder(members: [memberRef] as Set).build()

        when:
        groupUpdater.update(group, groupEntity)

        then: 'the member to remove was looked up in the current set of members of the group'
        1 * groupEntity.getMembers() >> ([member] as Set)
        and: 'the looked up member was removed'
        1 * groupEntity.removeMember(member)
        and: 'nothing else was changed'
        0 * groupEntity._
    }
}
