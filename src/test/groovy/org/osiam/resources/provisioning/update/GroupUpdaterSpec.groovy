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

package org.osiam.resources.provisioning.update

import org.osiam.resources.exception.ResourceExistsException
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MemberRef
import org.osiam.resources.scim.Meta
import org.osiam.storage.dao.GroupDao
import org.osiam.storage.dao.ResourceDao
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.ResourceEntity
import spock.lang.Specification
import spock.lang.Unroll

class GroupUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'

    def resourceUpdater = Mock(ResourceUpdater)
    def resourceDao = Mock(ResourceDao)
    def groupDao = Mock(GroupDao)
    def groupUpdater = new GroupUpdater(groupDao, resourceUpdater, resourceDao)

    Group group
    def groupEntity = Mock(GroupEntity)

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
        Meta meta = new Meta.Builder(attributes: ['displayName'] as Set).build()
        group = new Group.Builder(meta: meta).build()

        when:
        groupUpdater.update(group, groupEntity)

        then: 'the entity was not changed'
        0 * groupEntity._
    }

    def 'updating displayName is possible'() {
        given:
        def uuid = UUID.randomUUID()
        group = new Group.Builder(displayName: IRRELEVANT).build()

        when:
        groupUpdater.update(group, groupEntity)

        then: 'only the displayName was changed'
        1 * groupEntity.getId() >> uuid
        1 * groupEntity.setDisplayName(IRRELEVANT)
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
        Meta meta = new Meta.Builder(attributes: ['members'] as Set).build()
        group = new Group.Builder(meta: meta).build()

        when:
        groupUpdater.update(group, groupEntity)

        then: 'all members were removed'
        1 * groupEntity.removeAllMembers()
        and: 'nothing else was changed'
        0 * groupEntity._
    }

    def 'adding a member is possible'() {
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

    def 'removing a member is possible'() {
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

    def 'updating the displayName checks if the displayName already exists'() {
        given:
        def uuid = UUID.randomUUID()
        Group group = new Group.Builder(displayName: IRRELEVANT).build()
        groupEntity.getId() >> uuid

        when:
        groupUpdater.update(group, groupEntity)

        then:
        1 * groupDao.isDisplayNameAlreadyTaken(IRRELEVANT, uuid.toString())
    }

    def 'updating the displayName to an existing dislpayName raises exception'() {
        given:
        def uuid = UUID.randomUUID()
        Group group = new Group.Builder(displayName: IRRELEVANT).build()
        groupEntity.getId() >> uuid

        when:
        groupUpdater.update(group, groupEntity)

        then:
        1 * groupDao.isDisplayNameAlreadyTaken(IRRELEVANT, uuid.toString()) >> true
        thrown(ResourceExistsException)
    }
}
