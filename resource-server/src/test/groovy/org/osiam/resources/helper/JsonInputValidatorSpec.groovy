package org.osiam.resources.helper

import com.fasterxml.jackson.databind.JsonMappingException
import org.osiam.resources.scim.Constants
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

class JsonInputValidatorSpec extends Specification {

    def jsonInputValidator = new JsonInputValidator()
    HttpServletRequest httpServletRequestMock
    BufferedReader readerMock = Mock()

    def setup() {
        servletRequestWithMethod 'POST'
    }

    def 'validating user if JSON is valid works'() {
        given:
        def userJson = """{"userName":"irrelevant","password": "123","schemas" : ["$Constants.USER_CORE_SCHEMA"]}"""
        readerMock.readLine() >>> [userJson, null]

        when:
        def user = jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        user.userName == 'irrelevant'
    }

    def 'missing mandatory userName raises exception'() {
        given:
        def userJson = """{"schemas" : ["$Constants.USER_CORE_SCHEMA"]}"""
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        thrown(IllegalArgumentException)
    }

    def 'missing mandatory schema declaration for user raises exception'() {
        given:
        def userJson = '{"userName" : "irrelevant"}'
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        thrown(JsonMappingException)
    }

    def 'using PATCH without userName field works'() {
        given:
        def userJson = """{"password":"123", "schemas" : ["$Constants.USER_CORE_SCHEMA"]}"""
        servletRequestWithMethod 'PATCH'
        readerMock.readLine() >>> [userJson, null]

        expect:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)
    }

    def 'validating invalid JSON structure raises exception'() {
        given:
        def userJson = """{"schemas":["$Constants.USER_CORE_SCHEMA"],"userName":"irrelevant","password":"123"""
        readerMock.readLine() >>> [userJson, null]

        when:
        jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        thrown(IllegalArgumentException)
    }

    def 'removing uuid from user when using POST works'() {
        given:
        def userJson = """{"id":"irrelevant","schemas":["$Constants.USER_CORE_SCHEMA"],"userName":"irrelevant","password":"123"}"""
        readerMock.readLine() >>> [userJson, null]

        when:
        def user = jsonInputValidator.validateJsonUser(httpServletRequestMock)

        then:
        user.id == null
    }

    def 'validating group if JSON is valid works'() {
        given:
        def groupJson = """{"schemas":["$Constants.GROUP_CORE_SCHEMA"],"displayName":"display name"}"""
        readerMock.readLine() >>> [groupJson, null]

        when:
        def group = jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        group.displayName == 'display name'
    }

    def 'missing mandatory displayName raises exception'() {
        given:
        def groupJson = """{"schemas":["$Constants.GROUP_CORE_SCHEMA"],"members": []}"""
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        thrown(IllegalArgumentException)
    }

    def 'using PATCH without displayName works'() {
        given:
        def groupJson = """{"schemas":["$Constants.GROUP_CORE_SCHEMA"],"members": []}"""
        servletRequestWithMethod 'PATCH'
        readerMock.readLine() >>> [groupJson, null]

        expect:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

    }

    def 'trying to validate invalid JSON structure for group raises exception'() {
        given:
        def groupJson = '{"displayName":"display name"'
        readerMock.readLine() >>> [groupJson, null]

        when:
        jsonInputValidator.validateJsonGroup(httpServletRequestMock)

        then:
        thrown(IllegalArgumentException)
    }

    def servletRequestWithMethod(String requestMethodType) {
        httpServletRequestMock = Mock()
        httpServletRequestMock.getReader() >> readerMock
        httpServletRequestMock.getMethod() >> requestMethodType
    }
}