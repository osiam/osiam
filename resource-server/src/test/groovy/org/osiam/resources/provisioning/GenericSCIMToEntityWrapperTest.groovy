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

import org.osiam.storage.entities.AddressEntity
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.EntitlementsEntity
import org.osiam.storage.entities.ImEntity
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.PhoneNumberEntity
import org.osiam.storage.entities.PhotoEntity
import org.osiam.storage.entities.RolesEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.storage.entities.X509CertificateEntity
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.resources.scim.Name
import org.osiam.resources.scim.User
import spock.lang.Specification

class FakeSCIMEntities implements SCIMEntities {
    public static final FakeSCIMEntities ENTITIES = new FakeSCIMEntities();
    def fromString = new HashMap<>();


    private FakeSCIMEntities() {
        putEntity("emails", EmailEntity.class, true);
        putEntity("ims", ImEntity.class, true);
        putEntity("phonenumbers", PhoneNumberEntity.class, true);
        putEntity("photos", PhotoEntity.class, true);
        putEntity("entitlements", EntitlementsEntity.class, true);
        putEntity("roles", RolesEntity.class, true);
        putEntity("x509certificates", X509CertificateEntity.class, true);
        putEntity("addresses", AddressEntity.class, false);
    }

    def putEntity(String key, Class<?> clazz, boolean multiValue) {
        fromString.put(key, new SCIMEntities.Entity(clazz, multiValue));
    }

    @Override
    public SCIMEntities.Entity fromString(String key) {
        return fromString.get(key);
    }
}

class GenericSCIMToEntityWrapperTest extends Specification {

    def userTarget = GenericSCIMToEntityWrapper.For.USER

    def "should set an entity to given user attributes"() {
        given:
        def scimUser = new User.Builder("test").setActive(true)
                .setDisplayName("display")
                .setLocale("locale")
                .setName(new Name.Builder().build())
                .setNickName("nickname")
                .setPassword("password")
                .setPreferredLanguage("prefereedLanguage")
                .setProfileUrl("profileUrl")
                .setTimezone("time")
                .setTitle("title")
                .setUserType("userType")
                .setExternalId("externalid")
                .setId("id")
                .setMeta(new Meta.Builder().build())
                .build()

        def entity = new UserEntity()

        def underTest = new GenericSCIMToEntityWrapper(userTarget, scimUser, entity, GenericSCIMToEntityWrapper.Mode.PUT, FakeSCIMEntities.ENTITIES)
        when:
        underTest.setFields()


        then:
        scimUser.active == entity.active
        scimUser.displayName == entity.displayName
        scimUser.locale == entity.locale
        scimUser.name.familyName == entity.name.familyName
        scimUser.nickName == entity.nickName
        scimUser.password == entity.password
        scimUser.preferredLanguage == entity.preferredLanguage
        scimUser.profileUrl == entity.profileUrl
        scimUser.timezone == entity.timezone
        scimUser.title == entity.title
        scimUser.userType == entity.userType
        scimUser.userName == entity.userName
        scimUser.externalId == entity.externalId

        scimUser.x509Certificates.x509Certificate.size() == entity.x509Certificates.size()
        scimUser.roles.role.size() == entity.roles.size()
        scimUser.entitlements.entitlement.size() == entity.entitlements.size()
        !entity.groups
        scimUser.ims.im.size() == entity.ims.size()
        scimUser.emails.email.size() == entity.emails.size()
        scimUser.addresses.address.size() == entity.addresses.size()
        scimUser.phoneNumbers.phoneNumber.size() == entity.phoneNumbers.size()
        scimUser.photos.photo.size() == entity.photos.size()
    }

    def "should delete meta parameter when in PATCh mode"(){
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["displayName"] as Set).build()

        def user = new User.Builder("Harald").setMeta(meta).build()

        UserEntity entity = createEntityWithInternalId()
        entity.setUserName("hach")
        entity.setDisplayName("display it")
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)
        when:
        underTest.setFields()
        then:
        entity.userName == "Harald"
        entity.displayName == null
    }

    static UserEntity createEntityWithInternalId() {
        def entity = new UserEntity()
        entity.id = UUID.randomUUID()
        entity
    }


    def "should not delete meta parameter when they're required"(){
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["userName"] as Set).build()

        def user = new User.Builder().setMeta(meta).build()

        UserEntity entity = createEntityWithInternalId()
        entity.setUserName("hach")
        entity.setDisplayName("display it")
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)
        when:
        underTest.setFields()
        then:
        entity.userName == "hach"
    }

    def "should delete single attribute of a multi-value-attribute list in PATCH"() {

        def user = new User.Builder("test").setActive(true)
                .build()

        user.emails.add(new MultiValuedAttribute.Builder().setValue("email").setOperation("delete").build())

        user.entitlements.add(new MultiValuedAttribute.Builder().setValue("entitlement").setOperation("delete").build())

        user.ims.add(new MultiValuedAttribute.Builder().setValue("im").setOperation("delete").build())

        user.phoneNumbers.add(new MultiValuedAttribute.Builder().setValue("phonenumber").setOperation("delete").build())

        user.photos.add(new MultiValuedAttribute.Builder().setValue("photo.png").setOperation("delete").build())

        user.roles.add(new MultiValuedAttribute.Builder().setValue("role").setOperation("delete").build())
        user.x509Certificates.add(new MultiValuedAttribute.Builder().setValue("x509").setOperation("delete").build())


        def entity = createEntityWithInternalId()
        addListsToEntity(entity)
        entity.getEmails().add(new EmailEntity(value: "email2", type: "work", primary: false))
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)

        when:
        underTest.setFields()
        then:
        entity.getX509Certificates().empty
        !entity.getAddresses().empty
        entity.getEmails().size() == 1
        entity.getEntitlements().empty
        entity.getIms().empty
        entity.getPhoneNumbers().empty
        entity.getPhotos().empty
        entity.getRoles().empty
    }

    static addListsToEntity(UserEntity entity) {
        entity.getX509Certificates().add(new X509CertificateEntity(value: "x509"))
        entity.getAddresses().add(new AddressEntity())
        entity.getEmails().add(new EmailEntity(value: "email", type: "work", primary: false))
        entity.getEntitlements().add(new EntitlementsEntity(value: "entitlement"))
        entity.getIms().add(new ImEntity(value: "im", type: "icq"))
        entity.getPhoneNumbers().add(new PhoneNumberEntity(value: "phonenumber", type: "work"))
        entity.getPhotos().add(new PhotoEntity(value: "photo.png", type: "photo"))
        entity.getRoles().add(new RolesEntity(value: "role"))
    }

    def "should update parts of an complex attribute in PATCH mode"() {
        given:
        def user = new User.Builder().setName(new Name.Builder().setMiddleName("FNORD").build()).build()
        UserEntity entity = createEntityWithInternalId()
        def name = new NameEntity()
        name.setGivenName("Arthur")
        name.setFamilyName("Dent")
        name.setFormatted("Dent Arthur")
        name.honorificPrefix = "a"
        name.honorificSuffix = "b"
        name.setMiddleName('(")(°v°)(")')
        entity.setName(name)
        entity.setUserName("username")
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)

        when:
        underTest.setFields()
        then:
        entity.name.givenName == "Arthur"
        entity.name.familyName == "Dent"
        entity.name.middleName == "FNORD"
        entity.name.formatted == "Dent Arthur"
        entity.name.honorificPrefix == "a"
        entity.name.honorificSuffix == "b"
    }

    def "should remove parts of an complex attribute PATCH mode"() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["name.formatted"] as Set).build()
        def user = new User.Builder().setMeta(meta).build()
        UserEntity entity = createEntityWithInternalId()
        def name = new NameEntity()
        name.setGivenName("Arthur")
        name.setFamilyName("Dent")
        name.setFormatted("Dent Arthur")
        name.honorificPrefix = "a"
        name.honorificSuffix = "b"
        name.setMiddleName('(")(°v°)(")')
        entity.setName(name)
        entity.setUserName("username")
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)
        when:
        underTest.setFields()
        then:
        entity.name.givenName == "Arthur"
        entity.name.familyName == "Dent"
        entity.name.middleName == '(")(°v°)(")'
        entity.name.formatted == null
        entity.name.honorificPrefix == "a"
        entity.name.honorificSuffix == "b"
    }

    def "should ignore update in PATCH when the complex attribute is read only"() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["id.test"] as Set).build()
        def user = new User.Builder().setMeta(meta).build()
        UserEntity entity = createEntityWithInternalId()
        entity.setUserName("haha")
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)

        when:
        underTest.setFields()
        then:
        //should be thrown if it would continue a read only field, because UUID has no field test ...
        notThrown(NullPointerException)

    }

    def "should throw illegal argument exception instead of a null pointer exception if the field is unknown for the resource (e.g. familyName)"() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["familyName"] as Set).build()
        def user = new User.Builder().setMeta(meta).build()
        UserEntity entity = createEntityWithInternalId()
        entity.setUserName("haha")
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)

        when:
        underTest.setFields()

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "Field familyname is unknown."
    }

    def "should throw illegal argument exception instead of a null pointer exception if Name is not present for the resource"() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["name.familyName"] as Set).build()
        def user = new User.Builder().setMeta(meta).build()
        UserEntity entity = createEntityWithInternalId()
        entity.setUserName("haha")
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)

        when:
        underTest.setFields()

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "Field name is unknown."
    }

    def "should throw illegal argument exception instead of a null pointer exception if sub attributes of Name are unknown (e.g. name.firstName)"() {
        given:
        def meta = new Meta.Builder(null, null).setAttributes(["name.firstName"] as Set).build()
        def user = new User.Builder().setMeta(meta).build()
        UserEntity entity = createEntityWithInternalId()
        def name = new NameEntity()
        name.setGivenName("Harvey")
        name.setFamilyName("Dent")
        name.setFormatted("Dent Harvey")
        name.honorificPrefix = "Mr."
        name.honorificSuffix = "I"
        name.setMiddleName('Two-Face')
        entity.setName(name)
        entity.setUserName("haha")
        def underTest = new GenericSCIMToEntityWrapper(userTarget, user, entity, GenericSCIMToEntityWrapper.Mode.PATCH, FakeSCIMEntities.ENTITIES)

        when:
        underTest.setFields()

        then:
        def e = thrown(IllegalArgumentException)
        e.getMessage() == "Field firstname is unknown."
    }
}