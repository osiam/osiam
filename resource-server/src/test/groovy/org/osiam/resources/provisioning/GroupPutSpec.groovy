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

import org.osiam.resources.converter.GroupConverter
import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MemberRef
import org.osiam.storage.dao.GroupDao
import org.osiam.storage.entities.GroupEntity
import spock.lang.Specification

class GroupPutSpec extends Specification {

    GroupDao groupDao = Mock()
    GroupConverter groupConverter = Mock()

    SCIMGroupProvisioning underTest = new SCIMGroupProvisioning(groupDao: groupDao, groupConverter: groupConverter)

    Group group
    GroupEntity groupEntity = Mock()
    MemberRef member = new MemberRef.Builder()
            .setValue(memberId)
            .build()

    String groupUuid = UUID.randomUUID().toString()
    String memberId = UUID.randomUUID().toString()

    def setup() {
        group = new Group.Builder()
                .setDisplayName('irrelevant')
                .setId(groupUuid)
                .build();
    }

    def 'replacing a non-existant group raises exception'() {
        when:
        underTest.replace(groupUuid.toString(), group)

        then:
        1 * groupConverter.fromScim(_) >> { throw new ResourceNotFoundException('') }
        thrown(ResourceNotFoundException)
    }

    def 'replacing a group with a non-existant member raises exception'() {
        given:
        MemberRef member = new MemberRef.Builder()
                .setValue(memberId)
                .build()
        group = new Group.Builder(group)
                .setMembers([member] as Set)
                .build();

        when:
        underTest.replace(groupUuid.toString(), group)

        then:
        1 * groupConverter.fromScim(_) >> { throw new ResourceNotFoundException('') }
        thrown(ResourceNotFoundException)
    }

    def 'replacing a group with a member works as expected'() {
        group = new Group.Builder(group)
                .setMembers([member] as Set)
                .build();

        when:
        underTest.replace(groupUuid.toString(), group)

        then:
        1 * groupConverter.fromScim(_) >> groupEntity
        1 * groupDao.getById(groupUuid.toString()) >> groupEntity   // <- is not the same as above, but doesn't really matter here
        1 * groupDao.update(groupEntity) >> groupEntity
        1 * groupConverter.toScim(groupEntity) >> new Group.Builder(group).build();
        1 * groupEntity.touch()
    }

}
