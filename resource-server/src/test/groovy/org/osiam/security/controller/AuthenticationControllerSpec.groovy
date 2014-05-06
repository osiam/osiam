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

package org.osiam.security.controller

import org.osiam.resources.UserSpring
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.RoleEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification

class AuthenticationControllerSpec extends Specification {

    UserDao userDaoMock = Mock()
    AuthenticationController authenticationController = new AuthenticationController(userDao: userDaoMock)

    def "Should be able to load an User by his name an getting a UserSpring representation"() {
        given:
        def userEntityMock = Mock(UserEntity)
        def role = new RoleEntity()
        role.setValue("TestRole")
        def roles = [role] as Set
        def userId = UUID.randomUUID()

        when:
        def result = authenticationController.getUser("userName")

        then:
        1 * userDaoMock.getByUsername("userName") >> userEntityMock
        1 * userEntityMock.getRoles() >> roles
        1 * userEntityMock.getUserName() >> "userName"
        1 * userEntityMock.getPassword() >> "password"
        1 * userEntityMock.getId() >> userId
        1 * userEntityMock.getActive() >> true
        result instanceof UserSpring
        result.getUsername() == "userName"
        result.getPassword() == "password"
        result.getId() == userId.toString()
        result.isActive()
    }

}
