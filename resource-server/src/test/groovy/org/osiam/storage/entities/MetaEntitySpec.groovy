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

import org.osiam.storage.entities.MetaEntity
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.03.13
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
class MetaEntitySpec extends Specification {

    MetaEntity metaEntity = new MetaEntity()
    def date = Mock(Date)

    def "setter and getter for the Id should be present"() {
        when:
        metaEntity.setId(123456)

        then:
        metaEntity.getId() == 123456
    }

    def "setter and getter for the created field should be present"() {
        when:
        metaEntity.setCreated(date)

        then:
        metaEntity.getCreated() == date
    }

    def "should not throw NullpointerException if created is null"() {
        when:
        metaEntity.setCreated(null)

        then:
        metaEntity.getCreated() == null

    }

    def "setter and getter for the modified field should be present"() {
        when:
        metaEntity.setLastModified(date)

        then:
        metaEntity.getLastModified() == date
    }

    def "should not throw NullpointerException if last modified is null"() {
        when:
        metaEntity.setLastModified(null)

        then:
        metaEntity.getLastModified() == null
    }

    def "setter and getter for the location should be present"() {
        when:
        metaEntity.setLocation("https://example.com/Users/2819c223-7f76-453a-919d-413861904646")

        then:
        metaEntity.getLocation() == "https://example.com/Users/2819c223-7f76-453a-919d-413861904646"
    }

    def "setter and getter for the version should be present"() {
        when:
        metaEntity.setVersion("3694e05e9dff591")

        then:
        metaEntity.getVersion() == "3694e05e9dff591"
    }
}