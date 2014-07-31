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
 * THE SOFTWARE IS PROVIDED 'AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.osiam.resources.controller

import javax.servlet.http.HttpServletRequest

import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat
import org.osiam.resources.scim.User;
import org.osiam.security.authorization.AccessTokenValidationService
import org.osiam.security.helper.AccessTokenHelper;
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.UserEntity
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication

import spock.lang.Specification

class MeControllerSpec extends Specification {
    AccessTokenValidationService accessTokenValidationService = Mock()
    UserDao userDao = Mock()
    MeController underTest = new MeController(accessTokenValidationService: accessTokenValidationService, userDao: userDao)
    OAuth2Authentication authentication = Mock(OAuth2Authentication)
    HttpServletRequest request = Mock(HttpServletRequest)
    Authentication userAuthentication = Mock(Authentication)
    NameEntity name = new NameEntity(familyName: 'Prefect', givenName: 'Fnord', formatted: 'Fnord Prefect')
    UserEntity user = new UserEntity(active: true, emails: [
        new EmailEntity(primary: true, value: 'test@test.de')
    ],
    name: name, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
    locale: 'de_DE', userName: 'fpref')
    DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTime()

    def setup() {
        authentication.getUserAuthentication() >> userAuthentication
    }

    def 'should return correct facebook representation'() {
        given:
        User principal = new User.Builder('username').setId('theUserId').build()
        def userId = 'theUserId'

        when:
        def result = underTest.getInformation(request)

        then:
        1 * request.getParameter('access_token') >> 'access_token'
        1 * accessTokenValidationService.loadAuthentication('access_token') >> authentication
        1 * userAuthentication.getPrincipal() >> principal
        1 * userDao.getById(principal.id) >> user
        result.email == 'test@test.de'
        result.first_name == user.name.givenName
        result.last_name == user.name.familyName
        result.gender == 'not supported.'
        result.link == 'not supported.'
        result.locale == 'de_DE'
        result.name == user.name.formatted
        result.timezone == 2
        result.updated_time == dateTimeFormatter.print(user.getMeta().getLastModified().time)
        result.userName == 'fpref'
        result.id == user.getId().toString()
        result.isVerified()
    }

    def 'should not provide an email address if no primary email exists'() {
        given:
        UserEntity user = new UserEntity(active: true, name: name, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
        emails: [
            new EmailEntity(primary: false, value: 'test@test.de')
        ], locale: 'de_DE', userName: 'fpref')
        User principal = new User.Builder('username').setId('theUserId').build()
        def userId = 'theUserId'

        when:
        def result = underTest.getInformation(request)

        then:
        1 * request.getParameter('access_token') >> 'access_token'
        1 * accessTokenValidationService.loadAuthentication('access_token') >> authentication
        1 * userAuthentication.getPrincipal() >> principal
        1 * userDao.getById(principal.id) >> user
        result.getEmail() == null
    }

    def 'should get access_token in bearer format'() {
        given:
        User principal = new User.Builder('username').setId('theUserId').build()
        def userId = 'theUserId'
        def token = 'access_token'
        request.getHeader('Authorization') >> 'Bearer ' + token;

        when:
        def result = underTest.getInformation(request)

        then:
        1 * request.getParameter(token) >> null
        1 * accessTokenValidationService.loadAuthentication(token) >> authentication
        1 * userAuthentication.getPrincipal() >> principal
        1 * userDao.getById(principal.id) >> user
        result
    }

    def 'should throw exception if principal is not a String'() {
        given:
        def token = 'access_token'
        request.getHeader('Authorization') >> 'Bearer ' + token;

        when:
        underTest.getInformation(request)

        then:
        1 * request.getParameter('access_token') >> null
        1 * accessTokenValidationService.loadAuthentication(token) >> authentication
        1 * userAuthentication.getPrincipal() >> new Object()
        def e = thrown(IllegalArgumentException)
        e.message == 'User was not authenticated with OSIAM.'
    }

    def 'should not provide an email address if no emails were submitted'() {
        given:
        MetaEntity meta = new MetaEntity(GregorianCalendar.getInstance())
        def user = new UserEntity(active: true, name: name, id: UUID.randomUUID(), meta: meta, locale: 'de_DE',
                userName: 'fpref')
        User principal = new User.Builder('username').setId('theUserId').build()
        def userId = 'theUserId'
        def token = 'access_token'
        request.getHeader('Authorization') >> 'Bearer ' + token;

        when:
        def result = underTest.getInformation(request)

        then:
        1 * request.getParameter(token) >> null
        1 * accessTokenValidationService.loadAuthentication(token) >> authentication
        1 * userAuthentication.getPrincipal() >> principal
        1 * userDao.getById(principal.id) >> user
        result.getEmail() == null
    }

    def 'should not provide name, first name, last name if no name was submitted'() {
        given:
        def user = new UserEntity(active: true, emails: [
            new EmailEntity(primary: true, value: 'test@test.de')
        ],
        name: null, id: UUID.randomUUID(), meta: new MetaEntity(GregorianCalendar.getInstance()),
        locale: 'de_DE', userName: 'fpref')
        User principal = new User.Builder('username').setId('theUserId').build()
        def userId = 'theUserId'
        def token = 'access_token'
        request.getHeader('Authorization') >> 'Bearer ' + token;

        when:
        def result = underTest.getInformation(request)

        then:
        1 * request.getParameter(token) >> null
        1 * accessTokenValidationService.loadAuthentication(token) >> authentication
        1 * userAuthentication.getPrincipal() >> principal
        1 * userDao.getById(principal.id) >> user
        result.getName() == null
        result.getFirst_name() == null
        result.getLast_name() == null
    }
}