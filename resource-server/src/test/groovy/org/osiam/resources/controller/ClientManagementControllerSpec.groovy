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

import java.lang.reflect.Method

import org.osiam.storage.dao.ClientDao
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

import spock.lang.Specification

class ClientManagementControllerSpec extends Specification {

    def clientDao = Mock(ClientDao)
    def clientManagementController = new ClientManagementController(clientDao: clientDao)

    def "should contain a method to GET a client"() {
        given:
        Method method = ClientManagementController.class.getDeclaredMethod("getClient", String)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        clientManagementController.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        mapping.value() == ["/{id}"]
        mapping.method() == [RequestMethod.GET]
        body
        1 * clientDao.getClient("f47ac10b-58cc-4372-a567-0e02b2c3d479")
    }

    def "should contain a method to POST a client"() {
        given:
        Method method = ClientManagementController.class.getDeclaredMethod("create", String)
        def json = "{\"accessTokenValiditySeconds\":1337,\"refreshTokenValiditySeconds\":1337,\"redirectUri\":\"test\",\"scope\":[\"get\",\"post\",\"put\"]}"

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseBody body = method.getAnnotation(ResponseBody)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)

        clientManagementController.create(json)

        then:
        mapping.method() == [RequestMethod.POST]
        body
        defaultStatus.value() == HttpStatus.CREATED
        1 * clientDao.create(_)
    }

    def "should contain a method to DELETE a client"() {
        given:
        Method method = ClientManagementController.class.getDeclaredMethod("delete", String)

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        clientManagementController.delete("f47ac10b-58cc-4372-a567-0e02b2c3d479")

        then:
        mapping.method() == [RequestMethod.DELETE]
        defaultStatus.value() == HttpStatus.OK
        1 * clientDao.delete("f47ac10b-58cc-4372-a567-0e02b2c3d479")
    }

    def "should contain a method to update a client"() {
        given:
        Method method = ClientManagementController.class.getDeclaredMethod("update", String, String)
        def json = "{\"accessTokenValiditySeconds\":1337,\"refreshTokenValiditySeconds\":1337,\"redirectUri\":\"test\",\"scope\":[\"get\",\"post\",\"put\"]}"

        when:
        RequestMapping mapping = method.getAnnotation(RequestMapping)
        ResponseStatus defaultStatus = method.getAnnotation(ResponseStatus)
        ResponseBody body = method.getAnnotation(ResponseBody)
        clientManagementController.update('id', json)

        then:
        mapping.value() == ["/{id}"]
        mapping.method() == [RequestMethod.PUT]
        defaultStatus.value() == HttpStatus.OK
        body
        1 * clientDao.update(_, _)
    }
}