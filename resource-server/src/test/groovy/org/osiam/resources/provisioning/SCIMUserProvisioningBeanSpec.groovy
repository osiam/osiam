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

import org.antlr.v4.runtime.tree.ParseTree
import org.hibernate.hql.spi.FilterTranslator
import org.osiam.resources.converter.UserConverter
import org.osiam.resources.exceptions.ResourceExistsException
import org.osiam.resources.scim.User
import org.osiam.storage.dao.SearchResult
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.query.QueryFilterParser
import org.springframework.security.authentication.encoding.PasswordEncoder

import spock.lang.Specification

class SCIMUserProvisioningBeanSpec extends Specification {

    PasswordEncoder passwordEncoder = Mock()
    UserDao userDao = Mock()
    UserConverter userConverter = Mock()
    QueryFilterParser queryFilterParser = new QueryFilterParser()

    SCIMUserProvisioning scimUserProvisioningBean = new SCIMUserProvisioning(userDao: userDao,
            userConverter: userConverter, passwordEncoder: passwordEncoder, queryFilterParser: queryFilterParser)

    def 'should be possible to get a user by his id'() {
        given:
        def id = 'irrelevant'
        UserEntity userEntity = new UserEntity()

        when:
        scimUserProvisioningBean.getById(id)

        then:
        1 * userDao.getById(id) >> userEntity
        1 * userConverter.toScim(userEntity) >> new User()
    }

    def 'retrieving a user by id should remove its password'() {
        given:
        def id = 'irrelevant'
        UserEntity userEntity = new UserEntity()
        userDao.getById(id) >> userEntity
        userConverter.toScim(userEntity) >> new User(password: 'password')

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
        def userEntity = new UserEntity()
        def userScim = new User(userName: userName)

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
        def userEntity = new UserEntity()
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
        def idString = UUID.randomUUID().toString()
        def userScim = new User()

        UserEntity userEntity = Mock()
        userDao.getById(idString) >> Mock(UserEntity)

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
        def idString = UUID.randomUUID().toString()
        def userScim = new User()
        userConverter.fromScim(userScim) >> Mock(UserEntity)
        userConverter.toScim(_) >> userScim

        when:
        scimUserProvisioningBean.replace(idString, userScim)

        then:
        1 * userDao.getById(idString) >> Mock(UserEntity)
    }

    def 'replacing a user copies its unmodifiable values, i.e. internalId, meta and UUID'() {
        given:
        def id = UUID.randomUUID()
        def idString = id.toString()
        def internalId = 1L
        def meta = new MetaEntity()
        def userScim = new User()

        UserEntity existingEntity = Mock()
        UserEntity userEntity = Mock()
        userDao.getById(idString) >> existingEntity
        userConverter.fromScim(userScim) >> userEntity
        userConverter.toScim(_) >> userScim

        when:
        scimUserProvisioningBean.replace(idString, userScim)

        then:

        1 * existingEntity.getInternalId() >>internalId
        1 * existingEntity.getMeta() >> meta
        1 * existingEntity.getId() >> id
        1 * userEntity.setInternalId(internalId)
        1 * userEntity.setMeta(meta)
        1 * userEntity.setId(id)
    }

    def 'replacing a user with a password set, encodes and stores the new password' (){
        given:
        def id = UUID.randomUUID()
        def password = 'irrelevant'
        def hashedPassword = 'hashed password'

        User userScim = Mock()
        UserEntity userEntity = Mock()
        userDao.getById(id.toString()) >> new UserEntity(id: id)
        userConverter.fromScim(userScim) >> userEntity
        userConverter.toScim(_) >> userScim

        when:
        scimUserProvisioningBean.replace(id.toString(), userScim)

        then:
        3 * userScim.getPassword() >> password
        1 * userEntity.getId() >> id
        1 * passwordEncoder.encodePassword(password, id) >> hashedPassword
        1 * userEntity.setPassword(hashedPassword)
    }

    def 'replacing a user without a password set, copies the original password' () {
        given:
        def id = UUID.randomUUID()
        def password = 'irrelevant'
        def userScim = new User()

        UserEntity existingEntity = Mock()
        UserEntity userEntity = Mock()
        userDao.getById(id.toString()) >> existingEntity
        userConverter.fromScim(userScim) >> userEntity
        userConverter.toScim(_) >> userScim

        when:
        scimUserProvisioningBean.replace(id.toString(), userScim)

        then:
        1 * existingEntity.getPassword() >> password
        1 * userEntity.setPassword(password)
    }

    def 'replacing a user return the replaced user with its password removed' () {
        given:
        def id = UUID.randomUUID()
        def password = 'irrelevant'
        def userScim = new User()

        UserEntity existingEntity = Mock()
        UserEntity userEntity = Mock()

        userDao.getById(id.toString()) >> existingEntity
        userConverter.fromScim(userScim) >> userEntity
        userEntity.getPassword() >> password
        userEntity.getId() >> id
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
        User userScim = new User()
        SearchResult searchResult = new SearchResult([userEntity] as List, 1000L)
        userConverter.toScim(userEntity) >> userScim

        when:
        def result = scimUserProvisioningBean.search('userName eq "marissa"', 'userName', 'ascending', 100, 1)

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