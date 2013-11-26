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

package org.osiam.storage.dao

import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.InternalIdSkeleton
import org.osiam.storage.entities.RolesEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.filter.UserFilterParser
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.Query

class UserDaoSpec extends Specification {

    def em = Mock(EntityManager)
    def filterParserMock = Mock(UserFilterParser)
    def underTest = new UserDao(em: em, filterParser: filterParserMock)
    def userEntity = Mock(UserEntity)

    def setup() {
        userEntity.roles >> new HashSet<>(Arrays.asList(new RolesEntity(value: "test"), new RolesEntity()))
        userEntity.addresses >> new HashSet<>()
        userEntity.phoneNumbers >> new HashSet<>()
        userEntity.photos >> new HashSet<>()
        userEntity.entitlements >> new HashSet<>()
        userEntity.emails >> new HashSet<>()
        userEntity.x509Certificates >> new HashSet<>()
        userEntity.ims >> new HashSet<>()
    }

    def 'should get user by internal id'() {
        given:
        def internalId = UUID.randomUUID()
        def query = Mock(Query)
        def entity = new UserEntity()

        when:
        def result = underTest.getById(internalId.toString())

        then:
        1 * em.createNamedQuery('getById') >> query
        1 * query.getResultList() >> [entity]
        result == entity
    }

    def 'should throw an exception when no user is found by id'() {
        given:
        def internalId = UUID.randomUUID()
        def query = Mock(Query)
        def queryResults = []

        when:
        underTest.getById(internalId.toString())

        then:
        1 * em.createNamedQuery('getById') >> query
        1 * query.getResultList() >> queryResults
        def e = thrown(ResourceNotFoundException)
        e.message.contains(internalId.toString())
    }

    def 'should throw an exception when no user is found by name'() {
        given:
        def query = Mock(Query)
        def queryResults = []

        when:
        underTest.getByUsername('name')

        then:
        1 * em.createNamedQuery('getUserByUsername') >> query
        1 * query.setParameter('username', 'name')
        1 * query.getResultList() >> queryResults
        thrown(ResourceNotFoundException)

    }

    def 'should get user by username'() {
        given:
        def query = Mock(Query)
        def queryResults = [new UserEntity()]
        when:
        def result = underTest.getByUsername('name')
        then:
        1 * em.createNamedQuery('getUserByUsername') >> query
        1 * query.setParameter('username', 'name')
        1 * query.getResultList() >> queryResults
        result == queryResults.get(0)

    }

    def 'should be able to create a user'() {
        when:
        underTest.create(userEntity)

        then:
        1 * em.persist(userEntity)

    }

    def 'should first get an user than delete it'() {
        given:
        def entity = new UserEntity()
        def id = UUID.randomUUID().toString()
        def query = Mock(Query)
        when:
        underTest.delete(id)
        then:
        1 * em.createNamedQuery('getById') >> query
        1 * query.getResultList() >> [entity]
        1 * em.remove(entity)

    }

    def 'should wrap class cast exception to ResourceNotFoundException'() {
        given:
        def id = UUID.randomUUID().toString()
        def query = Mock(Query)

        InternalIdSkeleton internalIdSkeleton = new GroupEntity()
        when:
        underTest.getById(id)
        then:
        1 * em.createNamedQuery('getById') >> query
        1 * query.getResultList() >> [internalIdSkeleton]
        thrown(ResourceNotFoundException)
    }
}