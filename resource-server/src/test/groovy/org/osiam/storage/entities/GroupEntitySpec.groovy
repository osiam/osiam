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

package org.osiam.storage.entities

import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MultiValuedAttribute
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.03.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
class GroupEntitySpec extends Specification {

    GroupEntity groupEntity = new GroupEntity()

    def "setter and getter for the Id should be present"() {
        def id = UUID.randomUUID()
        when:
        groupEntity.setId(id)

        then:
        groupEntity.getId() == id
    }

    def "setter and getter for the display name should be present"() {
        when:
        groupEntity.setDisplayName("Tour Guides")

        then:
        groupEntity.getDisplayName() == "Tour Guides"
    }

    def "Group entity should set resourceType to Group"() {
        when:
        def result = new GroupEntity(displayName: "blubb", id: UUID.randomUUID())

        then:
        result.meta.resourceType == "Group"
    }

    def 'touch should update lastModified field of the meta object'() {
        given:
        groupEntity.meta.lastModified = new Date(0)

        when:
        groupEntity.touch()

        then:
        groupEntity.meta.lastModified.time > 0
    }
}