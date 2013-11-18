package org.osiam.resources.helper

import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 15.05.13
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
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
        def user = new User.Builder("username").setSchemas(["schemas:urn:scim:schemas:core:1.0"] as Set).build()
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
        result.getResources() == [[userName: 'username']]
    }

    def "should not filter the search result if attributes are empty"() {
        given:
        def user = new User.Builder("username").setSchemas(["schemas:urn:scim:schemas:core:1.0"] as Set).build()
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
        result.getResources() == [["schemas": ["schemas:urn:scim:schemas:core:1.0"], "userName": "username"]] as List

    }

    def "should return only data of a complex type"() {
        given:
        def set = ["schemas:urn:scim:schemas:core:1.0"] as Set
        String[] pA = ["meta", "created"]
        def param = ["attributes": pA, "count": 23, "startIndex": 23]

        def actualDate = GregorianCalendar.getInstance().getTime()
        def dateTimeFormatter = ISODateTimeFormat.dateTime();
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
        result.getResources() == [[meta: [created: created]]] as List
        result.getSchemas() == ['schemas:urn:scim:schemas:core:1.0'] as Set

    }
}