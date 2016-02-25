/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.scim

import spock.lang.Specification

class MetaSpec extends Specification {

    def "should be able to create without explicit created, last modified"() {
        given:
        def oldSysTime = System.currentTimeMillis()
        when:
        def meta = new Meta.Builder().build()
        then:
        meta.created >= meta.lastModified
        meta.created.time >= oldSysTime
    }

    def "should be able to set created and last modified to null without exception"() {
        when:
        def meta = new Meta.Builder(null, null).build()
        then:
        meta.created == null
        meta.lastModified == null
    }

    def "should be able to create with explicit created and last modified"() {
        given:
        def lastModified = System.currentTimeMillis() + 2342
        def createdOn = System.currentTimeMillis() - 2342
        when:
        def meta = new Meta.Builder(new Date(createdOn), new Date(lastModified)).build()
        then:
        meta.created.time == createdOn
        meta.lastModified.time == lastModified
    }

    def "should contain location and version"() {
        when:
        def meta = new Meta.Builder()
                .setLocation("dunno")
                .setVersion("version??")
                .setAttributes(new HashSet<String>())
                .build()
        then:
        meta.location == "dunno"
        meta.version == "version??"
    }

    def "attributes should should be ebale to get enriched"() {
        given:
        def meta = new Meta.Builder()
                .setLocation("dunno")
                .setVersion("version??")
                .build()

        when:
        meta.getAttributes().add("hallo")
        then:
        meta.getAttributes().size() == 1
    }

    def "should contain resourceType"() {
        when:
        def meta = new Meta.Builder().setResourceType("rt").build()
        then:
        meta.getResourceType() == "rt"
    }

    def 'creating a new MetaBuilder based on a old one is equals to the given'(){
        given:
        def meta = new Meta.Builder()
                .setLocation("dunno")
                .setVersion("version??")
                .setAttributes(['a', 'b'] as Set)
                .setResourceType('resourceType')
                .build()

        when:
        Meta newMeta = new Meta.Builder(meta).build()

        then:
        newMeta == meta
    }

    def 'creating a new Meta.Builder with a given null raises exception'(){
        when:
        new Meta.Builder(null)

        then:
        thrown(IllegalArgumentException)
    }
}
