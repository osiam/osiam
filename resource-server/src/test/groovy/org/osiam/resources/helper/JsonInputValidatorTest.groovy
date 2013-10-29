package org.osiam.resources.helper

import com.fasterxml.jackson.databind.JsonMappingException
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class JsonInputValidatorTest extends Specification {

    def jsonInputValidator = new JsonInputValidator()
    def httpServletRequestMock = Mock(HttpServletRequest)
    def readerMock = Mock(BufferedReader)
    def validUserJson = '{"userName":"irrelevant","password":"123", "schemas" : ["irrelevant"]}'

    def "should validate user without errors if json is valid"() {
        given:
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [validUserJson, null]

        when:
        def user = jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        user.userName == 'irrelevant'
    }

    def "should throw exception if the mandatory userName is missing"() {
        given:
        def userJson = '{"schemas" : ["irrelevant"]}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]
        httpServletRequestMock.getMethod() >> "POST"

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "The attribute userName is mandatory and MUST NOT be null"
    }

    def "should throw exception if the mandatory schemas declaration is missing"() {
        given:
        def userJson = '{"userName" : "irrelevant"}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]
        httpServletRequestMock.getMethod() >> "POST"

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        thrown(JsonMappingException)
    }

    def "should ignore the mandatory userName if method is PATCH"() {
        given:
        def userJson = '{"password":"123", "schemas" : ["irrelevant"]}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]
        httpServletRequestMock.getMethod() >> "PATCH"

        when:
        def result = jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        result
    }

    def "invalid json structure for user should throw exception"() {
        given:
        def userJson = '{"schemas":["urn:scim:schemas:core:1.0"],"userName":"irrelevant","password":"123"'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "The JSON structure is incorrect"
    }

    def "should validate group without errors if json is valid"() {
        given:
        def groupJson = '{"displayName":"display name"}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]

        when:
        def group = jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        group.displayName == 'display name'
    }

    def "should throw exception if the mandatory displayName is not present"() {
        given:
        def groupJson = ''
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]
        httpServletRequestMock.getMethod() >> "POST"

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == 'The attribute displayName is mandatory and MUST NOT be null.'
    }

    def "should ignore the mandatory displayName if method is PATCH"() {
        given:
        def groupJson = '{"members": []}'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]
        httpServletRequestMock.getMethod() >> "PATCH"

        when:
        def result = jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        result
    }

    def "invalid json structure for group should throw exception"() {
        given:
        def groupJson = '{"displayName":"display name"'
        httpServletRequestMock.getReader() >> readerMock
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "The JSON structure is incorrect"
    }
}