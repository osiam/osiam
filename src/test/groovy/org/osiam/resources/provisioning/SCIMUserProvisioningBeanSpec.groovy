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

package org.osiam.resources.provisioning

import org.osiam.resources.converter.UserConverter
import org.osiam.resources.exception.ResourceExistsException
import org.osiam.resources.scim.User
import org.osiam.storage.dao.SearchResult
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.UserEntity
import org.springframework.security.authentication.encoding.PasswordEncoder
import spock.lang.Specification

class SCIMUserProvisioningBeanSpec extends Specification {

    def passwordEncoder = Mock(PasswordEncoder)
    def userDao = Mock(UserDao)
    def userConverter = Mock(UserConverter)

    SCIMUserProvisioning scimUserProvisioningBean = new SCIMUserProvisioning(userDao: userDao,
            userConverter: userConverter, passwordEncoder: passwordEncoder)

    def 'should be possible to get a user by his id'() {
        given:
        def id = 'irrelevant'
        UserEntity userEntity = new UserEntity()

        when:
        scimUserProvisioningBean.getById(id)

        then:
        1 * userDao.getById(id) >> userEntity
        1 * userConverter.toScim(userEntity) >> new User.Builder().build()
    }

    def 'retrieving a user by id should remove its password'() {
        given:
        def id = 'irrelevant'
        UserEntity userEntity = new UserEntity()
        userDao.getById(id) >> userEntity
        userConverter.toScim(userEntity) >> new User.Builder(password: 'password').build()

        when:
        User user = scimUserProvisioningBean.getById(id)

        then:
        user.password == null
    }

    def 'should be possible to create a user with generated UUID as internalId'() {
        given:
        def scimUser = new User.Builder(userName: 'test', password: 'password').build()

        when:
        def user = scimUserProvisioningBean.create(scimUser)

        then:
        1 * passwordEncoder.encodePassword(_, _) >> "password"
        1 * userConverter.fromScim(_) >> new UserEntity(userName: 'test')
        1 * userDao.create(_)
        1 * userConverter.toScim(_) >> { UserEntity it ->
            new User.Builder(id: it.id).build()
        }
        user.id ==~ '[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}'
    }

    def 'should throw exception if user name already exists'() {
        given:
        def userName = 'irrelevant'
        def userScim = new User.Builder(userName: userName).build()

        when:
        scimUserProvisioningBean.create(userScim)

        then:
        1 * userDao.isUserNameAlreadyTaken(_) >> true
        def e = thrown(ResourceExistsException)
        e.getMessage().contains(userName)
    }

    def 'should throw exception if externalId already exists'() {
        given:
        def externalId = 'irrelevant'
        def userScim = new User.Builder().setExternalId(externalId).build()

        when:
        scimUserProvisioningBean.create(userScim)

        then:
        1 * userDao.isExternalIdAlreadyTaken(_) >> true
        def e = thrown(ResourceExistsException)
        e.getMessage().contains(externalId)
    }

    def 'replacing a user converts it from scim, updates last modified, updates the db and returns the replaced user as scim'() {
        given:
        def idString = UUID.randomUUID() as String
        def userScim = new User.Builder().build()

        def userEntity = Mock(UserEntity)
        userDao.getById(idString) >> new UserEntity(id: UUID.fromString(idString))

        when:
        scimUserProvisioningBean.replace(idString, userScim)

        then:
        1 * userConverter.fromScim(userScim) >> userEntity
        1 * userEntity.touch()
        1 * userDao.update(userEntity) >> userEntity
        1 * userConverter.toScim(userEntity) >> userScim
    }

    def 'replacing a user retrieves the original user from db'() {
        given:
        def idString = UUID.randomUUID() as String
        def userScim = new User.Builder().build()
        userConverter.fromScim(userScim) >> new UserEntity()
        userConverter.toScim(_) >> userScim

        when:
        scimUserProvisioningBean.replace(idString, userScim)

        then:
        1 * userDao.getById(idString) >> new UserEntity(id: UUID.fromString(idString))
    }

    def 'replacing a user copies its unmodifiable values, i.e. internalId, meta and UUID'() {
        given:
        def id = UUID.randomUUID()
        def idString = id.toString()
        def internalId = 1L
        def meta = new MetaEntity()
        def userScim = new User.Builder().build()

        def existingEntity = new UserEntity(id: id, meta: meta, internalId: internalId)
        def userEntity = Mock(UserEntity)
        userDao.getById(idString) >> existingEntity
        userConverter.fromScim(userScim) >> userEntity
        userConverter.toScim(_) >> userScim

        when:
        scimUserProvisioningBean.replace(idString, userScim)

        then:
        1 * userEntity.setInternalId(internalId)
        1 * userEntity.setMeta(meta)
        1 * userEntity.setId(id)
    }

    def 'replacing a user with a password set, encodes and stores the new password'() {
        given:
        def id = UUID.randomUUID()
        def password = 'irrelevant'
        def hashedPassword = 'hashed password'

        def userScim = new User.Builder().setPassword(password).build()
        def userEntity = Mock(UserEntity)
        userDao.getById(id.toString()) >> new UserEntity(id: id)
        userConverter.fromScim(userScim) >> userEntity
        userConverter.toScim(_) >> userScim

        when:
        scimUserProvisioningBean.replace(id.toString(), userScim)

        then:
        1 * userEntity.getId() >> id
        1 * passwordEncoder.encodePassword(password, id) >> hashedPassword
        1 * userEntity.setPassword(hashedPassword)
    }

    def 'replacing a user without a password set, copies the original password'() {
        given:
        def id = UUID.randomUUID()
        def password = 'irrelevant'
        def userScim = new User.Builder().build()

        def existingEntity = new UserEntity(id: id, password: password)
        def userEntity = new UserEntity()
        userDao.getById(id.toString()) >> existingEntity
        userConverter.fromScim(userScim) >> userEntity
        userConverter.toScim(_) >> userScim

        when:
        scimUserProvisioningBean.replace(id as String, userScim)

        then:
        userEntity.password == password
    }

    def 'replacing a user return the replaced user with its password removed'() {
        given:
        def id = UUID.randomUUID()
        def password = 'irrelevant'
        def userScim = new User.Builder().build()

        def existingEntity = new UserEntity(id: id, password: password)
        def userEntity = new UserEntity(id: id, password: password)

        userDao.getById(id.toString()) >> existingEntity
        userConverter.fromScim(userScim) >> userEntity
        userConverter.toScim(userEntity) >> { UserEntity it ->
            new User.Builder(id: it.getId(), password: it.getPassword()).build()
        }
        userDao.update(userEntity) >> userEntity

        when:
        User user = scimUserProvisioningBean.replace(id.toString(), userScim)

        then:
        user.password == null
    }

    def 'should call dao delete on delete'() {
        given:
        def id = UUID.randomUUID().toString()
        when:
        scimUserProvisioningBean.delete(id)
        then:
        1 * userDao.delete(id)
    }

    def 'searching for users calls search on dao'() {
        given:
        UserEntity userEntity = new UserEntity()
        User userScim = new User.Builder().build()
        SearchResult searchResult = new SearchResult([userEntity] as List, 1000L)
        userConverter.toScim(userEntity) >> userScim

        when:
        scimUserProvisioningBean.search('userName eq "marissa"', 'userName', 'ascending', 100, 1)

        then:
        1 * userDao.search(_, 'userName', 'ascending', 100, 0) >> searchResult
    }

    def 'creating a user returns the new user with its password removed'() {
        given:
        def scimUser = new User.Builder(userName: 'test', password: 'password').build()

        passwordEncoder.encodePassword(_, _) >> "password"
        userConverter.fromScim(_) >> new UserEntity(userName: 'test')
        userConverter.toScim(_) >> { UserEntity it ->
            new User.Builder(id: it.id, password: it.password).build()
        }

        when:
        def user = scimUserProvisioningBean.create(scimUser)

        then:
        user.password == null
    }
}
