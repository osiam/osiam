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

package org.osiam.resources.controller

import org.osiam.resources.helper.AttributesRemovalHelper
import org.osiam.resources.helper.JsonInputValidator
import org.osiam.resources.helper.RequestParamHelper
import org.osiam.resources.provisioning.SCIMGroupProvisioning
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.SCIMSearchResult
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.lang.reflect.Method

class GroupControllerSpec extends Specification {

    def httpServletRequest = Mock(HttpServletRequest)
    def provisioning = Mock(SCIMGroupProvisioning)
    def requestParamHelper = Mock(RequestParamHelper)
    def jsonInputValidator = Mock(JsonInputValidator)
    def attributesRemovalHelper = Mock(AttributesRemovalHelper)
    def underTest = new GroupController(scimGroupProvisioning: provisioning, requestParamHelper: requestParamHelper,
            jsonInputValidator: jsonInputValidator, attributesRemovalHelper: attributesRemovalHelper)
    def httpServletResponse = Mock(HttpServletResponse)
    Group group = new Group.Builder("group1").setId(UUID.randomUUID().toString()).build()

    def "should contain a method to POST a group"() {
        given:
        Method method = GroupController.class.getDeclaredMethod("create", HttpServletRequest, HttpServletResponse)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.POST]
        body
        defaultStatus.value() == HttpStatus.CREATED
    }

    def "should set LocationURI to the new created Group on POST"() {
        given:
        httpServletRequest.getRequestURL() >> new StringBuffer("http://host:port/deployment/Group")
        def uri = new URI("http://host:port/deployment/Group/" + group.id)
        jsonInputValidator.validateJsonGroup(httpServletRequest) >> group

        when:
        def result = underTest.create(httpServletRequest, httpServletResponse)

        then:
        1 * provisioning.create(group) >> group
        1 * httpServletResponse.setHeader("Location", uri.toASCIIString())
        result == group
    }

    def "should contain a method to GET a group"() {
        given:
        Method method = GroupController.class.getDeclaredMethod("get", String)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        then:
        mapping.method() == [RequestMethod.GET]
        mapping.value() == ['/{id}']
        body
    }

    def "should call provisioning get on get call"() {
        when:
        def result = underTest.get("id")
        then:
        1 * provisioning.getById("id") >> group
        result == group
    }

    def "should contain a method to DELETE a user"() {
        given:
        Method method = UserController.class.getDeclaredMethod("delete", String, HttpServletRequest)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.DELETE]
        defaultStatus.value() == HttpStatus.OK
    }

    def "should call provisioning delete on DELETE"() {
        when:
        underTest.delete("id")
        then:
        1 * provisioning.delete("id")

    }

    def "should contain a method to PUT a group"() {
        given:
        Method method = GroupController.class.getDeclaredMethod("replace", String, HttpServletRequest, HttpServletResponse)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.PUT]
        body
        defaultStatus.value() == HttpStatus.OK
    }

    def "should call provisioning on replace"() {
        given:
        def location = "http://host:port/deployment/Group/" + group.id
        jsonInputValidator.validateJsonGroup(httpServletRequest) >> group

        when:
        def result = underTest.replace(group.id, httpServletRequest, httpServletResponse)

        then:
        1 * httpServletRequest.getRequestURL() >> new StringBuffer(location)
        1 * provisioning.replace(group.id, group) >> group

        1 * httpServletResponse.setHeader("Location", location)
        result == group
    }

    def "should contain a method to PATCH a group"() {
        given:
        Method method = GroupController.class.getDeclaredMethod("update", String, HttpServletRequest, HttpServletResponse)
        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        then:
        mapping.method() == [RequestMethod.PATCH]
        body
        defaultStatus.value() == HttpStatus.OK
    }

    def "should call provisioning on update"() {
        given:
        def location = "http://host:port/deployment/Group/" + group.id
        jsonInputValidator.validateJsonGroup(httpServletRequest) >> group

        when:
        def result = underTest.update(group.id, httpServletRequest, httpServletResponse)

        then:
        1 * httpServletRequest.getRequestURL() >> new StringBuffer(location)
        1 * provisioning.update(group.id, group) >> group

        1 * httpServletResponse.setHeader("Location", location)
        result == group
    }

    def "should be able to search a group on /Group URI with GET method"() {
        given:
        Method method = GroupController.class.getDeclaredMethod("searchWithGet", HttpServletRequest)
        def servletRequestMock = Mock(HttpServletRequest)
        def map = Mock(Map)
        requestParamHelper.getRequestParameterValues(servletRequestMock) >> map

        map.get("filter") >> "filter"
        map.get("sortBy") >> "sortBy"
        map.get("sortOrder") >> "sortOrder"
        map.get("count") >> 10
        map.get("startIndex") >> 1

        def scimSearchResultMock = Mock(SCIMSearchResult)
        def set = ["schemas"] as Set
        provisioning.search("filter", "sortBy", "sortOrder", 10, 1) >> scimSearchResultMock
        scimSearchResultMock.getSchemas() >> set

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        underTest.searchWithGet(servletRequestMock)

        then:
        mapping.method() == [RequestMethod.GET]
        mapping.value() == []
        body
        1 * attributesRemovalHelper.removeSpecifiedAttributes(scimSearchResultMock, map)
    }

    def "should be able to search a group on /Group/.search URI with POST method"() {
        given:
        Method method = GroupController.class.getDeclaredMethod("searchWithPost", HttpServletRequest)
        def servletRequestMock = Mock(HttpServletRequest)
        def map = Mock(Map)
        requestParamHelper.getRequestParameterValues(servletRequestMock) >> map

        map.get("filter") >> "filter"
        map.get("sortBy") >> "sortBy"
        map.get("sortOrder") >> "sortOrder"
        map.get("count") >> 10
        map.get("startIndex") >> 1

        def scimSearchResultMock = Mock(SCIMSearchResult)
        def set = ["schemas"] as Set
        provisioning.search("filter", "sortBy", "sortOrder", 10, 1) >> scimSearchResultMock
        scimSearchResultMock.getSchemas() >> set

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        underTest.searchWithPost(servletRequestMock)

        then:
        mapping.value() == ["/.search"]
        mapping.method() == [RequestMethod.POST]
        body
        1 * attributesRemovalHelper.removeSpecifiedAttributes(scimSearchResultMock, map)

    }
}
