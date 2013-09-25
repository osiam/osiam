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

import org.osiam.resources.provisioning.SCIMUserProvisioningBean
import org.osiam.storage.dao.UserDAO
import org.osiam.storage.entities.UserEntity
import org.osiam.resources.exceptions.ResourceNotFoundException
import spock.lang.Specification

import javax.persistence.EntityManager
import javax.persistence.Query

class UserDeleteTest extends Specification {
    EntityManager em = Mock(EntityManager)
    def userDao = new UserDAO(em: em)
    SCIMUserProvisioningBean bean = new SCIMUserProvisioningBean(userDao: userDao)
    def uId = UUID.randomUUID()
    def id = uId.toString()
    def query = Mock(Query)


    def "should throw an org.osiam.resources.exceptions.ResourceNotFoundException when trying to delete unknown user"() {
        when:
        bean.delete(id)
        then:
        1 * em.createNamedQuery("getById") >> query
        1 * query.getResultList() >> []
        thrown(ResourceNotFoundException)


    }

    def "should not throw any Exception when trying to delete known user"() {
        given:
        def entity = new UserEntity()
        when:
        bean.delete(id)
        then:
        1 * em.createNamedQuery("getById") >> query
        1 * query.getResultList() >> [entity]
        1 * em.remove(entity)

    }


}
