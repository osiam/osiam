/*
 * Copyright (C) 2013 tarent AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * 'Software'), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.osiam.resources.provisioning

import org.osiam.resources.scim.Meta
import org.osiam.resources.converter.UserConverter
import org.osiam.resources.scim.Address
import org.osiam.resources.scim.Extension
import org.osiam.resources.scim.ExtensionFieldType
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.Name
import org.osiam.resources.scim.User
import org.osiam.storage.dao.ExtensionDao
import org.osiam.storage.dao.UserDao
import org.osiam.storage.entities.AddressEntity
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.EntitlementsEntity
import org.osiam.storage.entities.ExtensionEntity
import org.osiam.storage.entities.ExtensionFieldEntity
import org.osiam.storage.entities.GroupEntity
import org.osiam.storage.entities.ImEntity
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.PhoneNumberEntity
import org.osiam.storage.entities.PhotoEntity
import org.osiam.storage.entities.RolesEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.entities.X509CertificateEntity
import org.springframework.security.authentication.encoding.PasswordEncoder

import spock.lang.Specification

class UserPatchSpec extends Specification {

    UserDao userDao = Mock()
    UserConverter userConverter = Mock()
    PasswordEncoder passwordEncoder = Mock()
    ExtensionDao extensionDao = Mock()

    SCIMUserProvisioningBean scimUserProvisioningBean = new SCIMUserProvisioningBean(userDao: userDao,
            userConverter: userConverter, passwordEncoder: passwordEncoder, extensionDao: extensionDao)

    def uuid = UUID.randomUUID()
    def uuidAsString = uuid.toString()
    UserEntity entity = createEntityWithInternalId()

    def setup() {
        userDao.update(_) >> entity
        userConverter.fromScim(_) >> entity
    }

    def 'should delete single attribute of a multi-value-attribute list'() {
        def emails = new ArrayList()
        emails.add(new MultiValuedAttribute.Builder().setValue('email').setOperation('delete').build())

        def entitlements = new ArrayList()
        entitlements.add(new MultiValuedAttribute.Builder().setValue('entitlement').setOperation('delete').build())

        def ims = new ArrayList()
        ims.add(new MultiValuedAttribute.Builder().setValue('im').setOperation('delete').build())

        def numbers = new ArrayList()
        numbers.add(new MultiValuedAttribute.Builder().setValue('phonenumber').setOperation('delete').build())

        def photos = new ArrayList()
        photos.add(new MultiValuedAttribute.Builder().setValue('photo.png').setOperation('delete').build())

        def roles = new ArrayList()
        roles.add(new MultiValuedAttribute.Builder().setValue('role').setOperation('delete').build())
        def certificates = new ArrayList()
        certificates.add(new MultiValuedAttribute.Builder().setValue('x509').setOperation('delete').build())
        def user = new User.Builder('test').setActive(true)
                .setEmails(emails)
                .setEntitlements(entitlements)
                .setIms(ims)
                .setPhoneNumbers(numbers)
                .setPhotos(photos)
                .setRoles(roles)
                .setX509Certificates(certificates)
                .build()


        addListsToEntity(entity)
        entity.getEmails().add(new EmailEntity(value: 'email2', type: 'work', primary: false))

        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.getX509Certificates().empty
        !entity.getAddresses().empty
        entity.getEmails().size() == 1
        entity.getEntitlements().empty
        entity.getIms().empty
        entity.getPhoneNumbers().empty
        entity.getPhotos().empty
        entity.getRoles().empty
    }

    private void addListsToEntity(UserEntity entity) {
        entity.getX509Certificates().add(new X509CertificateEntity(value: 'x509'))
        entity.getAddresses().add(new AddressEntity())
        entity.getEmails().add(new EmailEntity(value: 'email', type: 'work', primary: false))
        entity.getEntitlements().add(new EntitlementsEntity(value: 'entitlement'))
        entity.getIms().add(new ImEntity(value: 'im', type: 'icq'))
        entity.getPhoneNumbers().add(new PhoneNumberEntity(value: 'phonenumber', type: 'work'))
        entity.getPhotos().add(new PhotoEntity(value: 'photo.png', type: 'photo'))
        entity.getRoles().add(new RolesEntity(value: 'role'))
    }


    def 'should delete all attributes of a multi-value-attribute list'() {
        def meta = new Meta.Builder(null, null).setAttributes([
            'addresses',
            'emails',
            'entitlements',
            'ims',
            'phonenumbers',
            'photos',
            'roles',
            'X509Certificates'] as Set).build()
        def user = new User.Builder('test').setActive(true)
                .setMeta(meta)
                .build()


        addListsToEntity(entity)
        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.getX509Certificates().empty
        entity.getAddresses().empty
        entity.getEmails().empty
        entity.getEntitlements().empty
        entity.getIms().empty
        entity.getPhoneNumbers().empty
        entity.getPhotos().empty
        entity.getRoles().empty
    }


    def 'should replace attributes of a multi-value-attribute list'() {
        def emails = new ArrayList()
        emails.add(new MultiValuedAttribute.Builder().setValue('email').setPrimary(true).setType('home').build())

        def entitlements = new ArrayList()
        entitlements.add(new MultiValuedAttribute.Builder().setValue('entitlement').setPrimary(true).setType('home').build())

        def ims = new ArrayList()
        ims.add(new MultiValuedAttribute.Builder().setValue('im').setPrimary(true).setType('icq').build())

        def numbers = new ArrayList()
        numbers.add(new MultiValuedAttribute.Builder().setValue('phonenumber').setPrimary(true).setType('home').build())

        def photos = new ArrayList()
        photos.add(new MultiValuedAttribute.Builder().setValue('photo.png').setPrimary(true).setType('photo').build())

        def roles = new ArrayList()
        roles.add(new MultiValuedAttribute.Builder().setValue('role').setPrimary(true).setType('home').build())
        def certificates = new ArrayList()
        certificates.add(new MultiValuedAttribute.Builder().setValue('x509').setPrimary(true).setType('home').build())
        def user = new User.Builder('test').setActive(true)
                .setEmails(emails)
                .setEntitlements(entitlements)
                .setIms(ims)
                .setPhoneNumbers(numbers)
                .setPhotos(photos)
                .setRoles(roles)
                .setX509Certificates(certificates)
                .build()


        addListsToEntity(entity)
        entity.getEmails().add(new EmailEntity(value: 'email2', type: 'work', primary: false))

        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.getX509Certificates().size() == 1
        entity.getAddresses().size() == 1
        entity.getEmails().size() == 2

        for (EmailEntity e : entity.getEmails()) {
            if (e.getValue() == 'email') {
                e.primary == true
                e.type == 'home'
            } else {
                e.primary == false
                e.type == 'work'
            }
        }
        entity.getEntitlements().size() == 1
        entity.getIms().size() == 1
        entity.getIms().first().type == 'icq'
        entity.getPhoneNumbers().size() == 1
        entity.getPhoneNumbers().first().type == 'home'
        entity.getPhotos().size() == 1
        entity.getPhotos().first().type == 'photo'

        entity.getRoles().size() == 1
    }

    def 'should delete and add a value to a multi-value-attribute list'() {
        def emails = new ArrayList()
        emails.add(new MultiValuedAttribute.Builder().setValue('email').setOperation('delete').build())
        emails.add(new MultiValuedAttribute.Builder().setValue('email2').setType('work').build())
        def user = new User.Builder('test').setActive(true)
                .setEmails(emails)
                .build()


        addListsToEntity(entity)

        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.getEmails().size() == 1
        entity.getEmails().first().value == 'email2'
    }


    def 'should replace a non Sub-Attribute able attribute of an user (e.q. addresses)'() {
        def addresses = new ArrayList()
        addresses.add(new Address.Builder().setStreetAddress('123 Elm Street').build())
        addresses.add(new Address.Builder().setStreetAddress('Kingroad 42').build())
        def user = new User.Builder('test').setActive(true)
                .setAddresses(addresses)
                .build()


        entity.addresses.add(new AddressEntity(type: 'work'))
        entity.addresses.add(new AddressEntity(type: 'home'))
        addListsToEntity(entity)

        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.getAddresses().size() == 2
        for (AddressEntity a : entity.getAddresses())
            assert a.streetAddress == '123 Elm Street' || a.streetAddress == 'Kingroad 42'
    }


    def 'should update a single attribute'() {
        given:
        def user = new User.Builder('username').setDisplayName('hallo').build()

        when:
        scimUserProvisioningBean.update(uuidAsString, user)

        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.userName == 'username'
        entity.displayName == 'hallo'
    }

    def 'should delete simple attributes'() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(['displayName'] as Set).build()
        def user = new User.Builder().setMeta(meta).build()

        entity.setUserName('username')
        entity.setDisplayName('display it')
        userDao.getById(uuidAsString) >> entity
        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        entity.userName == 'username'
        entity.displayName == null
    }

    def 'should ignore when trying to delete required parameters'() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(['userName'] as Set).build()
        def user = new User.Builder().setMeta(meta).build()

        entity.setUserName('username')
        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.userName == 'username'
    }

    def 'should delete and update simple attributes'() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(['displayName'] as Set).build()

        def user = new User.Builder('Harald').setMeta(meta).build()

        entity.setUserName('hach')
        entity.setDisplayName('display it')
        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.userName == 'Harald'
        entity.displayName == null
    }

    def 'updating a user with a password set, encodes and stores the new password' (){
        given:
        def password = 'irrelevant'
        def hashedPassword = 'hashed password'
        def user = new User(userName: 'userName', displayName: 'displayName', password: password)
        userDao.getById(uuidAsString) >> entity

        when:
        scimUserProvisioningBean.update(uuidAsString, user)

        then:
        1 * passwordEncoder.encodePassword(password, _) >> hashedPassword
        entity.getPassword() == hashedPassword
    }

    def 'updating a user with extension set works' (){
        given:
        def urn = 'irrelevant'
        def extensionFieldName = 'irrelevant'
        def extensionFieldValue = 'irrelevant'

        Extension extension = new Extension(urn)
        extension.addOrUpdateField(extensionFieldName, extensionFieldValue)
        User user = new User.Builder('userName').addExtension(extension).build()

        userDao.getById(uuidAsString) >> entity
        ExtensionFieldEntity extensionField = new ExtensionFieldEntity(name: extensionFieldName, type: ExtensionFieldType.STRING)
        extensionDao.getExtensionByUrn(urn) >> new ExtensionEntity(urn: urn, fields: [extensionField] as Set)

        when:
        scimUserProvisioningBean.update(uuidAsString, user)

        then:
        entity.getUserExtensions().size() == 1
        entity.getUserExtensions().first().value == extensionFieldValue
    }


    def 'should ignore update when simple-attribute is in meta'() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(['displayName'] as Set).build()

        def user = new User.Builder().setDisplayName('don\'t display that').setMeta(meta).build()

        entity.setUserName('Harald')
        entity.setDisplayName('display it')
        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.userName == 'Harald'
        entity.displayName == null
    }

    def 'should update parts of an complex attribute'() {
        given:
        def user = new User.Builder().setName(new Name.Builder().setMiddleName('FNORD').build()).build()

        def name = new NameEntity()
        name.setGivenName('Arthur')
        name.setFamilyName('Dent')
        name.setFormatted('Dent Arthur')
        name.honorificPrefix = 'a'
        name.honorificSuffix = 'b'
        name.setMiddleName('(\')(°v°)(\')')
        entity.setName(name)
        entity.setUserName('username')

        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.name.givenName == 'Arthur'
        entity.name.familyName == 'Dent'
        entity.name.middleName == 'FNORD'
        entity.name.formatted == 'Dent Arthur'
        entity.name.honorificPrefix == 'a'
        entity.name.honorificSuffix == 'b'
    }

    def 'should remove parts of an complex attribute'() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(['name.formatted'] as Set).build()
        def user = new User.Builder().setMeta(meta).build()

        def name = new NameEntity()
        name.setGivenName('Arthur')
        name.setFamilyName('Dent')
        name.setFormatted('Dent Arthur')
        name.honorificPrefix = 'a'
        name.honorificSuffix = 'b'
        name.setMiddleName('(\')(°v°)(\')')
        entity.setName(name)
        entity.setUserName('username')

        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.name.givenName == 'Arthur'
        entity.name.familyName == 'Dent'
        entity.name.middleName == '(\')(°v°)(\')'
        entity.name.formatted == null
        entity.name.honorificPrefix == 'a'
        entity.name.honorificSuffix == 'b'
    }

    def 'should ignore update when the complex attribute is read only'() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(['id.test'] as Set).build()
        def user = new User.Builder().setMeta(meta).build()

        entity.setUserName('haha')

        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        //should be thrown if it would continue a read only field, because UUID has no field test ...
        notThrown(NullPointerException)

    }

    def 'should ignore read-only attributes on modify'() {
        given:
        def user = new User.Builder().setGroups(new ArrayList()).build()
        user.getGroups().add(new MultiValuedAttribute.Builder().build())

        entity.setUserName('username')


        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.groups == [] as Set
    }

    def 'should ignore read-only attributes on delete'() {
        given:
        def attributes = ['groups'] as Set
        Meta meta = new Meta.Builder(null, null).setAttributes(attributes).build()
        def user = new User.Builder().setMeta(meta).build()


        entity.setUserName('username')
        entity.addToGroup(new GroupEntity(id: UUID.randomUUID()))

        when:
        scimUserProvisioningBean.update(uuidAsString, user)
        then:
        2 * userDao.getById(uuidAsString) >> entity
        entity.groups.size() == 1
    }

    def 'should set Meta.lastModified to actual date on update resource'() {
        given:
        def user = new User.Builder('username').setDisplayName('hallo').build()
        def lastModified = entity.getMeta().getLastModified()

        when:
        scimUserProvisioningBean.update(uuidAsString, user)

        then:
        2 * userDao.getById(uuidAsString) >> entity
        lastModified <= entity.getMeta().getLastModified()
    }

    def 'should set Meta.lastModified to actual date on replace resource'() {
        given:
        def user = new User.Builder('username').setDisplayName('hallo').build()
        def lastModified = entity.getMeta().getLastModified()

        when:
        scimUserProvisioningBean.replace(uuidAsString, user)

        then:
        1 * userDao.getById(uuidAsString) >> entity
        1 * userDao.update(entity) >> entity
        lastModified <= entity.getMeta().getLastModified()
    }

    private UserEntity createEntityWithInternalId() {
        def entity = new UserEntity()
        entity.userName = 'username'
        entity.id = uuid
        entity
    }
}
