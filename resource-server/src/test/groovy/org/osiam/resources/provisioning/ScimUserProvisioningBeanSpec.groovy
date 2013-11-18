package org.osiam.resources.provisioning

import org.osiam.resources.converter.UserConverter
import org.osiam.resources.exceptions.ResourceExistsException
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import org.osiam.storage.dao.UserDAO
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.UserEntity
import org.springframework.security.authentication.encoding.PasswordEncoder
import spock.lang.Ignore
import spock.lang.Specification

class ScimUserProvisioningBeanSpec extends Specification {

    PasswordEncoder passwordEncoder = Mock()
    UserDAO userDao = Mock()
    UserConverter userConverter = Mock()


    SCIMUserProvisioningBean scimUserProvisioningBean = new SCIMUserProvisioningBean(userDao: userDao,
            userConverter: userConverter,
            passwordEncoder: passwordEncoder)

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
        1 * userConverter.toScim(_) >> { UserEntity it -> new User.Builder(id: it.id).build() }
        user.id ==~ '[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}'
    }

    @Ignore('Temporarily ignored because of merge in propgress')
    def 'should throw exception if user name already exists'() {
        given:
        def exception = Mock(Exception)
        userDao.create(_) >> { throw exception }
        scimUser.getUserName() >> 'test'
        exception.getMessage() >> 'scim_user_username_key'

        when:
        scimUserProvisioningBean.create(scimUser)

        then:
        1 * userConverter.createFromScim(_) >> new UserEntity(userName: 'test')
        def e = thrown(ResourceExistsException)
        e.getMessage() == 'The user with name test already exists.'
    }

    @Ignore('Temporarily ignored because of merge in propgress')
    def 'should throw exception if externalId already exists'() {
        given:
        def exception = Mock(Exception)
        userDao.create(_) >> { throw exception }
        scimUser.getExternalId() >> 'irrelevant'
        exception.getMessage() >> 'scim_id_externalid_key'

        when:
        scimUserProvisioningBean.create(scimUser)

        then:
        1 * userConverter.createFromScim(_) >> new UserEntity()
        def e = thrown(ResourceExistsException)
        e.getMessage() == 'The user with the externalId irrelevant already exists.'
    }

    @Ignore('Temporarily ignored because of merge in propgress')
    def 'should get an user before update, set the expected fields, merge the result'() {
        given:
        def internalId = UUID.randomUUID()
        def scimUser = new User.Builder('test').build()
        def entity = new UserEntity(userName: 'username')
        entity.setId(internalId)

        when:
        scimUserProvisioningBean.replace(internalId.toString(), scimUser)
        then:
        1 * userConverter.createFromScim(scimUser, internalId.toString()) >> entity
        1 * userDao.update(entity) >> entity
    }

    def 'should wrap IllegalAccessException to an IllegalState'() {
        given:
        GenericSCIMToEntityWrapper setUserFields = Mock(GenericSCIMToEntityWrapper)

        when:
        scimUserProvisioningBean.setFieldsWrapException(setUserFields);
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
        def scimSearchResultMock = Mock(SCIMSearchResult)
        userDao.search('anyFilter', 'userName', 'ascending', 100, 1) >> scimSearchResultMock

        UserEntity userEntity = Mock()
        userEntity.getUserName() >> 'someName'
        userEntity.getId() >> UUID.randomUUID()
        userEntity.getMeta() >> new MetaEntity()
        def userList = [userEntity] as List
        scimSearchResultMock.getResources() >> userList
        scimSearchResultMock.getTotalResults() >> 1000.toLong()

        when:
        def result = scimUserProvisioningBean.search('anyFilter', 'userName', 'ascending', 100, 1)

        then:
        result.getTotalResults() == 1000.toLong()
    }
}