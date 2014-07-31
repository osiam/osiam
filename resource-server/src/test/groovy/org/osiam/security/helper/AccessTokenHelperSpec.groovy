/*
 * Copyright (C) 2014 tarent AG
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

package org.osiam.security.helper

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

class AccessTokenHelperSpec extends Specification {

    HttpServletRequest request = Mock(HttpServletRequest)

    def 'should throw exception when no access_token was submitted'() {
        given:
        request.getHeader('Authorization') >> null

        when:
        AccessTokenHelper.getBearerToken(request)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == 'No access_token submitted!'
    }

    def 'should get access_token in bearer format'() {
        given:
        def header = 'Bearer access token'

        when:
        def result = AccessTokenHelper.getBearerToken(request)

        then:
        1 * request.getHeader('Authorization') >> header
        result == 'access token'
    }
}