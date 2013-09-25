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

import org.osiam.storage.dao.GroupDAO
import org.osiam.resources.provisioning.SCIMGroupProvisioningBean
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MultiValuedAttribute
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.Query

class GroupPutTest extends Specification {
    EntityManager em = Mock(EntityManager)
    GroupDAO dao = new GroupDAO(em: em)
    def underTest = new SCIMGroupProvisioningBean(groupDAO: dao)
    def members = new HashSet()
    def internalId = UUID.randomUUID()
    def query = Mock(Query)
    def memberId = UUID.randomUUID().toString()




    def "should abort when group to replace not found"() {
        given:
        def queryResults = []
        members.add(new MultiValuedAttribute.Builder().setValue(internalId.toString()).build())
        def group = new Group.Builder().setMembers(members).build()
        when:
        underTest.replace(internalId.toString(), group)
        then:
        1 * em.createNamedQuery("getById") >> query
        1 * query.setParameter("id", internalId);
        1 * query.getResultList() >> queryResults
        def e = thrown(ResourceNotFoundException)
        e.message == "Resource " + internalId.toString() + " not found."

    }

    def "should abort when a member in group is not findable"() {
        given:
        def queryResults = []
        members.add(new MultiValuedAttribute.Builder().setValue(memberId.toString()).build())
        def group = new Group.Builder().setMembers(members).build()
        def groupToUpdate = [GroupEntity.fromScim(group)]
        when:
        underTest.replace(internalId.toString(), group)
        then:
        2 * em.createNamedQuery("getById") >> query
        2 * query.setParameter("id", _);
        2 * query.getResultList() >>> [groupToUpdate, queryResults]
        def e = thrown(ResourceNotFoundException)
        e.message == "Resource " + memberId + " not found."

    }



    def "should replace a group with known group member"() {
        given:
        members.add(new MultiValuedAttribute.Builder().setValue(memberId.toString()).build())
        def group = new Group.Builder().setMembers(members).build()
        def groupToUpdate = [GroupEntity.fromScim(group)]
        def queryResults = [GroupEntity.fromScim(group)]
        when:
        def result = underTest.replace(internalId.toString(), group)
        then:
        2 * em.createNamedQuery("getById") >> query
        2 * query.setParameter("id", _);
        2 * query.getResultList() >>> [groupToUpdate, queryResults]
        1 * em.merge(_) >> GroupEntity.fromScim(group)
        result.members.size() == 1
    }

    def "should replace a group with known group and user member"() {
        given:
        def memberId2 = UUID.randomUUID().toString()
        members.add(new MultiValuedAttribute.Builder().setValue(memberId.toString()).build())
        members.add(new MultiValuedAttribute.Builder().setValue(memberId2.toString()).build())
        def group = new Group.Builder().setMembers(members).build()
        def groupToUpdate = [GroupEntity.fromScim(group)]
        def userToUpdate = [new UserEntity(id: UUID.fromString(memberId2))]
        def queryResults = [GroupEntity.fromScim(group)]
        when:
        def result = underTest.replace(internalId.toString(), group)
        then:
        3 * em.createNamedQuery("getById") >> query
        3 * query.setParameter("id", _);
        3 * query.getResultList() >>> [groupToUpdate, queryResults, userToUpdate ]
        1 * em.merge(_) >> GroupEntity.fromScim(group)
        result.members.size() == 2
    }


    def "should replace a group without member"() {
        given:
        def group = new Group.Builder().build()
        def groupToUpdate = [GroupEntity.fromScim(group)]
        when:
        def result = underTest.replace(internalId.toString(), group)
        then:
        1 * em.createNamedQuery("getById") >> query
        1 * query.setParameter("id", _);
        1 * query.getResultList() >> groupToUpdate
        1 * em.merge(_) >> GroupEntity.fromScim(group)
        result.members.size() == 0
    }


}
