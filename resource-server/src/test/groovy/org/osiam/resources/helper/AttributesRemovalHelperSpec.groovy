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

package org.osiam.resources.helper

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.scim.Constants
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import spock.lang.Specification

class AttributesRemovalHelperSpec extends Specification {

    def attributesRemovalHelperTest = new AttributesRemovalHelper()

    def "should not throw exception if result list is empty"() {
        given:
        def userList = [] as List<User>
        def parameterMapMock = Mock(Map)
        def attributes = ["userName"] as String[]

        parameterMapMock.get("attributes") >> attributes

        def scimSearchResult = Mock(SCIMSearchResult)

        scimSearchResult.getResources() >> userList
        scimSearchResult.getTotalResults() >> 1337

        when:
        def result = attributesRemovalHelperTest.removeSpecifiedAttributes(scimSearchResult, parameterMapMock)

        then:
        result.startIndex == 0
        result.itemsPerPage == 0
        result.totalResults == 1337
        result.getResources() == userList
    }

    def "should return Json string with additional values for searches on users, groups and filtering for userName"() {
        given:
        def user = new User.Builder("username").setSchemas([
                Constants.USER_CORE_SCHEMA] as Set).build()
        def userList = [user] as List<User>
        def parameterMapMock = Mock(Map)
        def attributes = ["userName"] as String[]

        parameterMapMock.get("attributes") >> attributes

        def scimSearchResult = Mock(SCIMSearchResult)

        scimSearchResult.getResources() >> userList
        scimSearchResult.getTotalResults() >> 1337

        when:
        def result = attributesRemovalHelperTest.removeSpecifiedAttributes(scimSearchResult, parameterMapMock)

        then:
        result.startIndex == 0
        result.itemsPerPage == 0
        result.totalResults == 1337
        result.getResources() == [[schemas: [Constants.USER_CORE_SCHEMA], userName: 'username']]
    }

    def "should not filter the search result if attributes are empty"() {
        given:
        def user = new User.Builder("username").setSchemas([
                "schemas:urn:ietf:params:scim:schemas:core:2.0"] as Set).build()
        def userList = [user] as List<User>
        def parameterMapMock = Mock(Map)
        def attributes = [] as String[]

        parameterMapMock.get("attributes") >> attributes

        def scimSearchResult = Mock(SCIMSearchResult)

        scimSearchResult.getResources() >> userList
        scimSearchResult.getTotalResults() >> 1337

        when:
        def result = attributesRemovalHelperTest.removeSpecifiedAttributes(scimSearchResult, parameterMapMock)

        then:
        result.startIndex == 0
        result.itemsPerPage == 0
        result.totalResults == 1337
        result.getResources() == [
                ["schemas"   : [
                        "schemas:urn:ietf:params:scim:schemas:core:2.0"
                ], "userName": "username"]] as List
    }

    def "should return only data of a complex type"() {
        given:
        def set = [
                Constants.USER_CORE_SCHEMA] as Set
        String[] pA = ["meta", "created"]
        def param = ["attributes": pA, "count": 23, "startIndex": 23]

        def actualDate = GregorianCalendar.getInstance().getTime()
        def dateTimeFormatter = ISODateTimeFormat.dateTime()
        def date = new DateTime(actualDate)
        def created = dateTimeFormatter.print(date)

        def user = new User.Builder("username").setMeta(new Meta.Builder(actualDate, null).build()).build()
        def scimSearchResult = new SCIMSearchResult([user], 1, 23, 1, set)

        when:
        def result = attributesRemovalHelperTest.removeSpecifiedAttributes(scimSearchResult, param)

        then:
        result.startIndex == 1
        result.itemsPerPage == 23
        result.totalResults == 1
        result.getResources() == [[meta: [created: created], schemas: [Constants.USER_CORE_SCHEMA]]] as List
        result.getSchemas() == [
                Constants.USER_CORE_SCHEMA] as Set
    }
}
