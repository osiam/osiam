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

package org.osiam.resources.scim

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.restassured.path.json.JsonPath
import spock.lang.Shared
import spock.lang.Specification

import static com.jayway.restassured.path.json.JsonPath.from

class MemberRefSpec extends Specification {

    @Shared
    ObjectMapper mapper = new ObjectMapper()

    def 'serializing member ref results in correct json'() {
        given:
        MemberRef memberRef = new MemberRef.Builder()
                .setReference('irrelevant')
                .build();

        when:
        JsonPath json = com.jayway.restassured.path.json.JsonPath.from(mapper.writeValueAsString(memberRef))

        then:
        json.get('$ref') == 'irrelevant'
    }

    def 'deserializing member ref results in correct MemberRef object'() {
        given:
        String json = '{"$ref":"irrelevant"}'

        when:
        MemberRef memberRef = mapper.readValue(json, MemberRef)

        then:
        memberRef.getReference() == 'irrelevant'
    }
}
