package org.osiam.resources.provisioning

import org.osiam.resources.converter.*
import org.osiam.resources.exceptions.ResourceExistsException
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import org.osiam.storage.dao.UserDAO
import org.osiam.storage.entities.MetaEntity
import org.osiam.storage.entities.UserEntity
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 19.03.13
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
class ScimUserProvisioningBeanSpec extends Specification {

    def userDao = Mock(UserDAO)
    def userEntity = Mock(UserEntity)
    def scimUser = Mock(User)

    X509CertificateConverter x509CertificateConverter = Mock(X509CertificateConverter)
    RoleConverter roleConverter = Mock(RoleConverter)
    PhotoConverter photoConverter = Mock(PhotoConverter)
    PhoneNumberConverter phoneNumberConverter = Mock(PhoneNumberConverter)
    ImConverter imConverter = Mock(ImConverter)
    EntitlementConverter entitlementConverter = Mock(EntitlementConverter)
    EmailConverter emailConverter = Mock(EmailConverter)
    AddressConverter addressConverter = Mock(AddressConverter)
    NameConverter nameConverter = Mock(NameConverter)
    ExtensionConverter extensionConverter = Mock(ExtensionConverter)
    MetaConverter metaConverter = Mock(MetaConverter)

    UserConverter userConverter = new UserConverter(
            x509CertificateConverter: x509CertificateConverter,
            roleConverter: roleConverter,
            photoConverter: photoConverter,
            phoneNumberConverter: phoneNumberConverter,
            imConverter: imConverter,
            entitlementConverter: entitlementConverter,
            emailConverter: emailConverter,
            addressConverter: addressConverter,
            nameConverter: nameConverter,
            extensionConverter: extensionConverter,
            metaConverter: metaConverter,
            userDao: userDao
    )

    SCIMUserProvisioningBean scimUserProvisioningBean = new SCIMUserProvisioningBean(userDao: userDao, userConverter: userConverter)

    @Ignore('This test had been fixed, for some reason a commit went astray')
    def "should be possible to get an user by his id"() {
        given:
        userEntity.getUserName() >> 'test1234'
        userEntity.getId() >> UUID.randomUUID()
        userEntity.getMeta() >> new MetaEntity()
        userDao.getById("1234") >> userEntity
        userConverter.toScim(userEntity) >> scimUser

        when:
        def user = scimUserProvisioningBean.getById("1234")

        then:
        user == scimUser
    }

    @Ignore('Temporarily ignored because of merge in propgress')
    def "should be possible to create a user with generated UUID as internalId"() {
        given:
        def scimUser = new User.Builder('test').build()

        when:
        def user = scimUserProvisioningBean.create(scimUser)

        then:
        1 * userConverter.fromScim(_) >> new UserEntity(userName: 'test')
        user.id ==~ "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
    }

    @Ignore('Temporarily ignored because of merge in propgress')
    def "should throw exception if user name already exists"() {
        given:
        def exception = Mock(Exception)
        userDao.create(_) >> { throw exception }
        scimUser.getUserName() >> 'test'
        exception.getMessage() >> "scim_user_username_key"

        when:
        scimUserProvisioningBean.create(scimUser)

        then:
        1 * userConverter.createFromScim(_) >> new UserEntity(userName: 'test')
        def e = thrown(ResourceExistsException)
        e.getMessage() == "The user with name test already exists."
    }

    @Ignore('Temporarily ignored because of merge in propgress')
    def "should throw exception if externalId already exists"() {
        given:
        def exception = Mock(Exception)
        userDao.create(_) >> { throw exception }
        scimUser.getExternalId() >> "irrelevant"
        exception.getMessage() >> "scim_id_externalid_key"

        when:
        scimUserProvisioningBean.create(scimUser)

        then:
        1 * userConverter.createFromScim(_) >> new UserEntity()
        def e = thrown(ResourceExistsException)
        e.getMessage() == "The user with the externalId irrelevant already exists."
    }

    @Ignore('Temporarily ignored because of merge in propgress')
    def "should get an user before update, set the expected fields, merge the result"() {
        given:
        def internalId = UUID.randomUUID()
        def scimUser = new User.Builder("test").build()
        def entity = new UserEntity(userName: "username")
        entity.setId(internalId)

        when:
        scimUserProvisioningBean.replace(internalId.toString(), scimUser)
        then:
        1 * userConverter.createFromScim(scimUser, internalId.toString()) >> entity
        1 * userDao.update(entity) >> entity
    }

    def "should wrap IllegalAccessException to an IllegalState"() {
        given:
        GenericSCIMToEntityWrapper setUserFields = Mock(GenericSCIMToEntityWrapper)

        when:
        scimUserProvisioningBean.setFieldsWrapException(setUserFields);
        then:
        1 * setUserFields.setFields() >> { throw new IllegalAccessException("Blubb") }
        def e = thrown(IllegalStateException)
        e.message == "This should not happen."

    }

    def "should call dao delete on delete"() {
        given:
        def id = UUID.randomUUID().toString()
        when:
        scimUserProvisioningBean.delete(id)
        then:
        1 * userDao.delete(id)
    }

    @Ignore('Temporarily ignored because of merge in propgress')
    def "should call dao search on search"() {
        given:
        def scimSearchResultMock = Mock(SCIMSearchResult)
        userDao.search("anyFilter", "userName", "ascending", 100, 1) >> scimSearchResultMock

        def userEntityMock = Mock(UserEntity)
        def user = Mock(User)
        def userList = [userEntityMock] as List
        scimSearchResultMock.getResources() >> userList
        scimSearchResultMock.getTotalResults() >> 1000.toLong()

        when:
        def result = scimUserProvisioningBean.search("anyFilter", "userName", "ascending", 100, 1)

        then:
        result != null
        result.getTotalResults() == 1000.toLong()
    }
}