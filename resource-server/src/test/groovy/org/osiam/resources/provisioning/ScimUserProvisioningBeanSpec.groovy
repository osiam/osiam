package org.osiam.resources.provisioning

import org.osiam.resources.converter.UserConverter
import org.osiam.resources.exceptions.ResourceExistsException
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import org.osiam.storage.dao.SearchResult
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.UserEntity
import org.springframework.security.authentication.encoding.PasswordEncoder

import spock.lang.Ignore
import spock.lang.Specification

class ScimUserProvisioningBeanSpec extends Specification {

    PasswordEncoder passwordEncoder = Mock()
    UserDao userDao = Mock()
    UserConverter userConverter = Mock()

    SCIMUserProvisioningBean scimUserProvisioningBean = new SCIMUserProvisioningBean(userDao: userDao,
    userConverter: userConverter, passwordEncoder: passwordEncoder)

    def 'should be possible to get an user by his id'() {

        when:
        scimUserProvisioningBean.getById('1234')

        then:
        1 * userDao.getById('1234') >> new UserEntity()
        1 * userConverter.toScim(_)
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
        1 * userConverter.fromScim(userScim) >> userEntity
        1 * userDao.create(userEntity) >> { throw new Exception('scim_user_username_key') }
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
        1 * userConverter.fromScim(userScim) >> userEntity
        1 * userDao.create(userEntity) >> { throw new Exception('scim_id_externalid_key') }
        def e = thrown(ResourceExistsException)
        e.getMessage().contains(externalId)
    }

    def 'should get a user before replace, set the unmodifiable fields, merge the result and return scim user'() {
        given:
        def id = UUID.randomUUID()
        def idString = id.toString()
        def internalId = 1L
        def meta = new MetaEntity()
        def userScim = new User()

        UserEntity existingEntity = Mock()
        UserEntity userEntity = Mock()

        when:
        scimUserProvisioningBean.replace(idString, userScim)

        then:
        1 * userDao.getById(idString) >> existingEntity
        1 * userConverter.fromScim(userScim) >> userEntity

        1 * existingEntity.getInternalId() >>internalId
        1 * existingEntity.getMeta() >> meta
        1 * existingEntity.getId() >> id
        1 * userEntity.setInternalId(internalId)
        1 * userEntity.setMeta(meta)
        1 * userEntity.setId(id)

        1 * userDao.update(userEntity) >> userEntity
        1 * userConverter.toScim(userEntity)
    }

    def 'should wrap IllegalAccessException to an IllegalState'() {
        given:
        GenericSCIMToEntityWrapper setUserFields = Mock(GenericSCIMToEntityWrapper)

        when:
        scimUserProvisioningBean.setFieldsWrapException(setUserFields)
        then:
        1 * setUserFields.setFields() >> { throw new IllegalAccessException('Blubb') }
        def e = thrown(IllegalStateException)
        e.message == 'This should not happen.'
    }

    def 'should call dao delete on delete'() {
        given:
        def id = UUID.randomUUID().toString()
        when:
        scimUserProvisioningBean.delete(id)
        then:
        1 * userDao.delete(id)
    }

    def 'should call dao search on search'() {
        given:
        UserEntity userEntity = new UserEntity()
        User userScim = new User()
        SearchResult searchResult = new SearchResult([userEntity] as List, 1000L)
        userDao.search('anyFilter', 'userName', 'ascending', 100, 1) >> searchResult

        when:
        def result = scimUserProvisioningBean.search('anyFilter', 'userName', 'ascending', 100, 1)

        then:
        1 * userDao.search('anyFilter', 'userName', 'ascending', 100, 0) >> searchResult
        1 * userConverter.toScim(userEntity) >> userScim
        result.getResources().size() == 1
        result.getStartIndex() == 1
        result.getItemsPerPage() == 100
        result.getTotalResults() == 1000L
    }
}