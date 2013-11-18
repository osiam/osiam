package org.osiam.resources.controller

import org.osiam.storage.dao.ClientDao
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 27.05.13
 * Time: 11:09
 * To change this template use File | Settings | File Templates.
 */
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