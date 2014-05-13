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

package org.osiam.resources.provisioning

import javax.persistence.NoResultException

import org.antlr.v4.runtime.tree.ParseTree
import org.apache.tika.parser.ParseContext
import org.osiam.resources.converter.GroupConverter
import org.osiam.resources.exceptions.ResourceExistsException
import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.resources.provisioning.update.GroupUpdater
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MemberRef
import org.osiam.storage.dao.GroupDao
import org.osiam.storage.dao.SearchResult
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.query.QueryFilterParser
import org.springframework.dao.DataIntegrityViolationException

import spock.lang.Specification

class SCIMGroupProvisioningBeanSpec extends Specification {

    GroupDao groupDao = Mock()
    GroupConverter groupConverter = Mock()
    GroupUpdater groupUpdater = Mock()
    QueryFilterParser queryFilterParser = new QueryFilterParser()

    SCIMGroupProvisioning scimGroupProvisioning = new SCIMGroupProvisioning(groupDao: groupDao, groupConverter: groupConverter,
    groupUpdater: groupUpdater, queryFilterParser: queryFilterParser)

    Group group = Mock()
    GroupEntity groupEntity = Mock()

    def groupUuid = UUID.randomUUID().toString()
    def memberId = UUID.randomUUID().toString()

    def 'retrieving a group works as expected'() {
        when:
        scimGroupProvisioning.getById(groupUuid)

        then:
        1 * groupDao.getById(groupUuid)
        1 * groupConverter.toScim(_)
    }

    def 'retrieving a non-existant group raises exception'() {
        when:
        scimGroupProvisioning.getById(groupUuid)

        then:
        1 * groupDao.getById(groupUuid) >> { throw new ResourceNotFoundException('') }
        thrown(ResourceNotFoundException)
    }

    def 'creating a group with a non-existant member raises exception'() {
        given:
        MemberRef member = new MemberRef.Builder()
                .setValue(memberId)
                .build()
        group = new Group.Builder()
                .setMembers([member] as Set)
                .build()

        when:
        scimGroupProvisioning.create(group)

        then:
        1 * groupConverter.fromScim(_) >> { throw new ResourceNotFoundException('') }
        thrown(ResourceNotFoundException)
    }

    def 'creating an existing group raises exception'() {
        when:
        scimGroupProvisioning.create(group)

        then:
        1 * groupDao.isDisplayNameAlreadyTaken(_) >> true
        thrown(ResourceExistsException)
    }

    def 'creating a group works as expected'() {
        when:
        scimGroupProvisioning.create(group)

        then:
        1 * groupConverter.fromScim(group) >> groupEntity
        1 * groupEntity.setId(_)
        1 * groupDao.create(groupEntity)
        1 * groupConverter.toScim(groupEntity)
    }

    def 'replacing a non-existing group raises exception'() {
        when:
        scimGroupProvisioning.replace(groupUuid.toString(), group)

        then:
        1 * groupConverter.fromScim(_) >> { throw new ResourceNotFoundException('') }
        thrown(ResourceNotFoundException)
    }

    def 'replacing a group with a non-existing member raises exception'() {
        given:
        MemberRef member = new MemberRef.Builder()
                .setValue(memberId)
                .build()
        group = new Group.Builder()
                .setMembers([member] as Set)
                .build()

        when:
        scimGroupProvisioning.replace(groupUuid.toString(), group)

        then:
        1 * groupConverter.fromScim(_) >> { throw new ResourceNotFoundException('') }
        thrown(ResourceNotFoundException)
    }

    def 'replacing a group with a member works as expected'() {
        MemberRef member = new MemberRef.Builder()
                .setValue(memberId)
                .build()
        group = new Group.Builder()
                .setMembers([member] as Set)
                .build()

        when:
        scimGroupProvisioning.replace(groupUuid.toString(), group)

        then:
        1 * groupConverter.fromScim(_) >> groupEntity
        1 * groupDao.getById(groupUuid.toString()) >> groupEntity   // <- is not the same as above, but doesn't really matter here
        1 * groupDao.update(groupEntity) >> groupEntity
        1 * groupConverter.toScim(groupEntity) >> new Group.Builder(group).build()
        1 * groupEntity.touch()
    }

    def 'deleting an unknown group raises exception'() {

        when:
        scimGroupProvisioning.delete(groupUuid)

        then:
        1 * groupDao.delete(groupUuid) >> { throw new NoResultException() }
        def rnfe = thrown(ResourceNotFoundException)

        rnfe.getMessage().with {
            contains('Group')
            contains(groupUuid)
        }
    }

    def 'deleting a user calls userDao.delete()'() {

        when:
        scimGroupProvisioning.delete(groupUuid)

        then:
        1 * groupDao.delete(groupUuid)
    }

    def 'searching for groups calls groupDao.search(), converts each entity to scim and returns the SCIM search result'() {
        given:
        def groupList = [groupEntity] as List
        
        when:
        def result = scimGroupProvisioning.search("externalId eq \"group\"", "userName", "ascending", 100, 1)

        then:
        1 * groupDao.search(_, "userName", "ascending", 100, 0) >> new SearchResult(groupList, 1000)
        1 * groupConverter.toScim(groupEntity) >> group

        result.resources.size() == 1
        result.resources.first() == group
        result.startIndex == 1
        result.itemsPerPage == 100
        result.totalResults == 1000.toLong()
    }

    def 'updating a group retrieves the entity, updates it and converts it back to scim'() {
        given:

        when:
        scimGroupProvisioning.update(groupUuid, group)

        then:
        1 * groupDao.getById(groupUuid) >> groupEntity
        1 * groupUpdater.update(group, groupEntity)
        1 * groupEntity.touch()
        1 * groupConverter.toScim(groupEntity)
    }
}
