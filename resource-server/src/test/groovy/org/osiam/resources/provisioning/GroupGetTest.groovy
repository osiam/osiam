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

import javax.persistence.EntityManager
import javax.persistence.Query

import org.osiam.resources.converter.GroupConverter;
import org.osiam.resources.exceptions.ResourceNotFoundException
import org.osiam.resources.scim.Group
import org.osiam.storage.dao.GroupDAO
import org.osiam.storage.entities.GroupEntity

import spock.lang.Ignore
import spock.lang.Specification

class GroupGetTest extends Specification {

    GroupDAO groupDao = Mock()
    GroupConverter groupConverter = Mock()

    SCIMGroupProvisioningBean groupProvisioningBean = new SCIMGroupProvisioningBean(groupDAO: groupDao, groupConverter: groupConverter)

    String groupUuid = UUID.randomUUID().toString()
    
    def 'retrieving a group works as expected'() {
        when:
        groupProvisioningBean.getById(groupUuid)
        
        then:
        1 * groupDao.getById(groupUuid)
        1 * groupConverter.toScim(_)
    }
    
    def 'retrieving a non-existant group raises exception'() {
        when:
        groupProvisioningBean.getById(groupUuid)
        
        then:
        1 * groupDao.getById(groupUuid) >> { throw new ResourceNotFoundException('') }
        thrown(ResourceNotFoundException)
    }
}
