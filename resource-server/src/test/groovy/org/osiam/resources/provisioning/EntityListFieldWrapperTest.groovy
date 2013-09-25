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

import org.osiam.resources.scim.Address
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.User
import org.osiam.storage.entities.AddressEntity
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.EntitlementsEntity
import org.osiam.storage.entities.ImEntity
import org.osiam.storage.entities.PhoneNumberEntity
import org.osiam.storage.entities.PhotoEntity
import org.osiam.storage.entities.RolesEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.entities.X509CertificateEntity
import spock.lang.Specification

class EntityListFieldWrapperTest extends Specification {
    def mode = GenericSCIMToEntityWrapper.Mode.PUT
    def "should set entity lists"() {
        given:
        def any = new HashSet()
        any.add("ha")



        def scimUser = new User.Builder("test").setActive(true)
                .build()

        scimUser.addresses.address.add(new Address.Builder().setDisplay("haha").build())

        scimUser.emails.add(new MultiValuedAttribute.Builder().setPrimary(true).build())
        scimUser.emails.add(new MultiValuedAttribute.Builder().build())

        scimUser.entitlements.add(new MultiValuedAttribute.Builder().build())
        scimUser.entitlements.add(new MultiValuedAttribute.Builder().setValue("ha").build())

        scimUser.ims.add(new MultiValuedAttribute.Builder().build())

        scimUser.phoneNumbers.add(new MultiValuedAttribute.Builder().build())

        scimUser.photos.add(new MultiValuedAttribute.Builder().build())

        scimUser.roles.add(new MultiValuedAttribute.Builder().build())
        scimUser.x509Certificates.add(new MultiValuedAttribute.Builder().build())


        def entity = new UserEntity()
        initializeSetsOfEntity(entity)

        def underTest = new EntityListFieldWrapper(entity, mode)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)


        when:
        underTest.set(scimUser.addresses, new SCIMEntities.Entity(AddressEntity, false), aha.get("addresses"))
        underTest.set(scimUser.emails, new SCIMEntities.Entity(EmailEntity, true), aha.get("emails"))
        underTest.set(scimUser.entitlements, new SCIMEntities.Entity(EntitlementsEntity, true),aha.get("entitlements"))
        underTest.set(scimUser.ims, new SCIMEntities.Entity(ImEntity, true),aha.get("ims"))
        underTest.set(scimUser.phoneNumbers, new SCIMEntities.Entity(PhoneNumberEntity, true),aha.get("phonenumbers"))
        underTest.set(scimUser.photos, new SCIMEntities.Entity(PhotoEntity, true),aha.get("photos"))
        underTest.set(scimUser.roles, new SCIMEntities.Entity(RolesEntity, true),aha.get("roles"))
        underTest.set(scimUser.x509Certificates, new SCIMEntities.Entity(X509CertificateEntity, true),aha.get("x509certificates"))

        then:
        scimUser.x509Certificates.size() == entity.x509Certificates.size()
        scimUser.roles.size() == entity.roles.size()
        scimUser.entitlements.size() == entity.entitlements.size()
        scimUser.ims.size() == entity.ims.size()
        scimUser.emails.size() == entity.emails.size()
        scimUser.addresses.size() == entity.addresses.size()
        scimUser.phoneNumbers.size() == entity.phoneNumbers.size()
        scimUser.photos.size() == entity.photos.size()
    }

    def "should fail silently if a user is trying to delete an unknown field"(){
        given:

        def entity = new UserEntity()
        initializeSetsOfEntity(entity)
        entity.getEmails().add(new EmailEntity(value: "there"))

        def underTest = new EntityListFieldWrapper(entity, GenericSCIMToEntityWrapper.Mode.PATCH)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)

        def emails = new ArrayList()
        emails.add(new MultiValuedAttribute.Builder().setOperation("delete").setValue("notThere").build())
        emails.add(new MultiValuedAttribute.Builder().setOperation("delete").setValue("there").build())
        when:
        underTest.set(emails, new SCIMEntities.Entity(EmailEntity, true), aha.get("emails"))
        then:
        entity.getEmails().size() == 0



    }

    private void initializeSetsOfEntity(UserEntity entity) {
        entity.getX509Certificates()
        entity.getAddresses()
        entity.getEmails()
        entity.getEntitlements()
        entity.getIms()
        entity.getPhoneNumbers()
        entity.getPhotos()
        entity.getRoles()
    }

    def "should not set null but empty"() {
        given:

        def entity = new UserEntity()
        def underTest = new EntityListFieldWrapper(entity, mode)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)
        initializeSetsOfEntity(entity)
        when:
        underTest.set(null, new SCIMEntities.Entity(AddressEntity, false), aha.get("addresses"))
        underTest.set(null, new SCIMEntities.Entity(EmailEntity, true), aha.get("emails"))
        underTest.set(null, new SCIMEntities.Entity(EntitlementsEntity, true),aha.get("entitlements"))
        underTest.set(null, new SCIMEntities.Entity(ImEntity, true),aha.get("ims"))
        underTest.set(null, new SCIMEntities.Entity(PhoneNumberEntity, true),aha.get("phonenumbers"))
        underTest.set(null, new SCIMEntities.Entity(PhotoEntity, true),aha.get("photos"))
        underTest.set(null, new SCIMEntities.Entity(RolesEntity, true),aha.get("roles"))
        underTest.set(null, new SCIMEntities.Entity(X509CertificateEntity, true),aha.get("x509certificates"))



        then:
        entity.x509Certificates.size() == 0
        entity.roles.size() == 0
        entity.entitlements.size() == 0
        entity.ims.size() == 0
        entity.emails.size() == 0
        entity.addresses.size() == 0
        entity.phoneNumbers.size() == 0
        entity.photos.size() == 0
    }

    def "should replace attributes of a multi-value-attribute list"() {
        def emails = new ArrayList()
        emails.add(new MultiValuedAttribute.Builder().setValue("email").setPrimary(true).setType("home").build())
        emails.add(new MultiValuedAttribute.Builder().setValue("email3").setPrimary(true).setType("home").build())

        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)
        def entity = GenericSCIMToEntityWrapperTest.createEntityWithInternalId()
        GenericSCIMToEntityWrapperTest.addListsToEntity(entity)
        entity.getEmails().add(new EmailEntity(value: "email2", type: "work", primary: false))
        def underTest = new EntityListFieldWrapper(entity, GenericSCIMToEntityWrapper.Mode.PATCH)
        when:
        underTest.set(emails, new SCIMEntities.Entity(EmailEntity, true), aha.get("emails"))
        then:

        entity.getEmails().size() == 3

        for (EmailEntity e : entity.getEmails()) {
            if (e.getValue() == "email" || e.getValue() == "email3") {
                e.primary == true
                e.type == "home"
            } else {
                e.primary == false
                e.type == "work"
            }
        }
    }

    def "should wrap InstantiationException to IllegalStateException"(){
        given:
        def attr = Mock(SCIMEntities.Entity)
        attr.isNotMultiValue() >> {throw new InstantiationException("moep")}
        def underTest = new EntityListFieldWrapper(null, GenericSCIMToEntityWrapper.Mode.PATCH)
        when:
        underTest.wrapExceptions("test", attr, null)
        then:
        def e = thrown(IllegalStateException)
        e.message == "java.lang.InstantiationException: moep"
    }

    def "should wrap IllegalAccessException to IllegalStateException"(){
        given:
        def attr = Mock(SCIMEntities.Entity)
        attr.isNotMultiValue() >> {throw new IllegalAccessException("moep")}
        def underTest = new EntityListFieldWrapper(null, GenericSCIMToEntityWrapper.Mode.PATCH)
        when:
        underTest.wrapExceptions("test", attr, null)
        then:
        def e = thrown(IllegalStateException)
        e.message == "java.lang.IllegalAccessException: moep"
    }


    // | IllegalAccessException


}
