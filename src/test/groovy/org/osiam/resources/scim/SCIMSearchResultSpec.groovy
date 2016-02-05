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

import com.fasterxml.jackson.databind.type.TypeFactory
import org.osiam.test.util.JsonFixturesHelper
import spock.lang.Specification

class SCIMSearchResultSpec extends Specification {

    def 'Mapping a SCIMSearchResult from JSON works'() {
        given:
        def json = '''{
          "totalResults":2,
          "itemsPerPage":100,
          "startIndex":1,
          "schemas":["urn:ietf:params:scim:api:messages:2.0:ListResponse"],
          "Resources":[
            {
              "id":"834b410a-943b-4c80-817a-4465aed037bc",
              "externalId":"bjensen",
              "meta":{
                "created":"2013-08-08T19:51:34.498+02:00",
                "lastModified":"2013-08-08T19:51:34.498+02:00",
                "resourceType":"User"
              },
              "schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],
              "userName":"bjensen",
              "name":{
                "formatted":"Ms. Barbara J Jensen III",
                "familyName":"Jensen",
                "givenName":"Barbara"
              },
              "displayName":"BarbaraJ.",
              "nickName":"Barbara",
              "profileUrl":"http://babaraJ.com",
              "title":"Dr.",
              "userType":"user",
              "preferredLanguage":"de",
              "locale":"de",
              "timezone":"UTC",
              "groups":[{
                "value":"69e1a5dc-89be-4343-976c-b5541af249f4",
                "display":"test_group01"
              }],
              "active":true
            }, {
              "id":"cef9452e-00a9-4cec-a086-d171374ffbef",
              "meta":{
                "created":"2011-10-10T00:00:00.000+02:00",
                "lastModified":"2011-10-10T00:00:00.000+02:00",
                "resourceType":"User"
              },
              "schemas":["urn:ietf:params:scim:schemas:core:2.0:User"],
              "userName":"marissa",
              "active":true
            }
          ]
        }'''
        def mapper = new JsonFixturesHelper().configuredObjectMapper()

        when:
        SCIMSearchResult<User> result = mapper.readValue(json, TypeFactory.defaultInstance().constructParametrizedType(
                SCIMSearchResult, SCIMSearchResult, User))

        then:
        result.getTotalResults() == 2
        result.getResources()[0].getId() == "834b410a-943b-4c80-817a-4465aed037bc"
        result.getResources()[1].getId() == "cef9452e-00a9-4cec-a086-d171374ffbef"
    }
}
