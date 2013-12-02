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
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.Query

class GroupDaoSpec extends Specification {

    EntityManager em = Mock(EntityManager)

    Query query = Mock(Query)
    InternalIdSkeleton internalIdSkeleton = new GroupEntity(id: UUID.randomUUID())

    def underTest = new GroupDao(em: em)
    String id = UUID.randomUUID().toString()

    def setup() {
        em.createNamedQuery('getById') >> query
    }

    def 'persisting a group without members works'() {
        given:
        def entity = new GroupEntity()
        when:
        underTest.create(entity)
        then:
        1 * em.persist(entity)
    }

    // This test might well be senseless
    def 'retrieving a group works'() {
        when:
        def result = underTest.getById(id)
        then:
        1 * query.getResultList() >> [internalIdSkeleton]
        result == internalIdSkeleton
    }

    def 'a class cast exception is wraped into a ResourceNotFoundException'() {
        given:
        internalIdSkeleton = new UserEntity()
        when:
        underTest.getById(id)
        then:
        1 * query.getResultList() >> [internalIdSkeleton]
        thrown(ResourceNotFoundException)
    }

    def 'deleting a group first retrieves it, then deletes it'() {
        when:
        underTest.delete(id)
        then:
        1 * query.getResultList() >> [internalIdSkeleton]
        1 * em.remove(internalIdSkeleton)
    }

    def 'deleting an unknown group raises exception'() {
        when:
        underTest.delete(id)
        then:
        1 * query.getResultList() >> []
        0 * em.remove(internalIdSkeleton)
        thrown(ResourceNotFoundException)
    }
}
