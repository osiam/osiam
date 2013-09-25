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

import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.UserEntity
import org.osiam.resources.provisioning.EntityFieldWrapper
import org.osiam.resources.provisioning.GenericSCIMToEntityWrapper
import org.osiam.resources.provisioning.GetFieldsOfInputAndTarget
import org.osiam.resources.scim.Name
import org.osiam.resources.scim.User
import spock.lang.Specification

class EntityFieldWrapperTest extends Specification {
    def mode = GenericSCIMToEntityWrapper.Mode.PUT

    def "should not update a single field of an entity when value is null and mode is patch"() {
        given:
        def scimUser = new User.Builder("test")
                .setDisplayName("display")
                .setName(new Name.Builder().setFamilyName("Prefect").build())
                .build()

        def entity = new UserEntity()
        entity.setDisplayName("Not Null")
        def mode = GenericSCIMToEntityWrapper.Mode.PATCH
        def underTest = new EntityFieldWrapper(entity, mode)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)

        when:
        underTest.updateSingleField(aha.get("displayname"), null, "displayname")

        then:
        entity.displayName == "Not Null"
    }

    def "should update a single field of an entity with the given value"() {
        given:
        def scimUser = new User.Builder("test")
                .setDisplayName("display")
                .setName(new Name.Builder().setFamilyName("Prefect").build())
                .build()

        def entity = new UserEntity()
        def underTest = new EntityFieldWrapper(entity, mode)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)

        when:
        underTest.updateSingleField(aha.get("displayname"), scimUser.displayName, "displayname")
        underTest.updateSingleField(aha.get("name"), scimUser.getName(), "name")
        then:
        scimUser.displayName == entity.displayName
        scimUser.name.familyName == entity.name.familyName
    }

    def "should update parts of a complex field when in patch mode"() {
        given:
        def scimUser = new User.Builder("test")
                .setDisplayName("display")
                .setName(new Name.Builder()
                .setFamilyName("Prefect")
                .setHonorificPrefix("prefix")
                .setHonorificSuffix("suffix")
                .build()).build()

        def entity = new UserEntity()
        entity.setName(new NameEntity())
        def underTest = new EntityFieldWrapper(entity, GenericSCIMToEntityWrapper.Mode.PATCH)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)

        when:
        underTest.updateSingleField(aha.get("displayname"), scimUser.displayName, "displayname")
        underTest.updateSingleField(aha.get("name"), scimUser.getName(), "name")
        then:
        scimUser.displayName == entity.displayName
        scimUser.name.familyName == entity.name.familyName
        null == entity.name.formatted
        null == entity.name.givenName
        scimUser.name.honorificPrefix == entity.name.honorificPrefix
        scimUser.name.honorificSuffix == entity.name.honorificSuffix
        scimUser.name.middleName == entity.name.middleName
    }

    def "should update all parts of a complex field"() {
        given:
        def scimUser = new User.Builder("test")
                .setName(new Name.Builder()
                .setFamilyName("Prefect")
                .setFormatted("formated")
                .setGivenName("given")
                .setHonorificPrefix("prefix")
                .setHonorificSuffix("suffix")
                .setMiddleName("middle")
                .build()).build()

        def entity = new UserEntity()
        entity.setName(new NameEntity())
        def underTest = new EntityFieldWrapper(entity, GenericSCIMToEntityWrapper.Mode.PATCH)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)

        when:
        underTest.updateSingleField(aha.get("displayname"), scimUser.displayName, "displayname")
        underTest.updateSingleField(aha.get("name"), scimUser.getName(), "name")
        then:
        scimUser.displayName == entity.displayName
        scimUser.name.familyName == entity.name.familyName
        scimUser.name.formatted == entity.name.formatted
        scimUser.name.givenName == entity.name.givenName
        scimUser.name.honorificPrefix == entity.name.honorificPrefix
        scimUser.name.honorificSuffix == entity.name.honorificSuffix
        scimUser.name.middleName == entity.name.middleName
    }

    def "should insert new name object when the one hold by entity is null"() {
        given:
        def scimUser = new User.Builder("test")
                .setName(new Name.Builder()
                .setFamilyName("Prefect")
                .setFormatted("formated")
                .setGivenName("given")
                .setHonorificPrefix("prefix")
                .setHonorificSuffix("suffix")
                .setMiddleName("middle")
                .build()).build()

        def entity = new UserEntity()
        entity.setName(null)
        def underTest = new EntityFieldWrapper(entity, GenericSCIMToEntityWrapper.Mode.PATCH)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)

        when:
        underTest.updateSingleField(aha.get("displayname"), scimUser.displayName, "displayname")
        underTest.updateSingleField(aha.get("name"), scimUser.getName(), "name")
        then:
        scimUser.displayName == entity.displayName
        scimUser.name.familyName == entity.name.familyName
        scimUser.name.formatted == entity.name.formatted
        scimUser.name.givenName == entity.name.givenName
        scimUser.name.honorificPrefix == entity.name.honorificPrefix
        scimUser.name.honorificSuffix == entity.name.honorificSuffix
        scimUser.name.middleName == entity.name.middleName
    }



    def "should update a given password of an entity"() {
        given:
        def scimUser = new User.Builder("test")
                .setPassword("hallo")
                .build()

        def entity = new UserEntity()
        entity.setPassword("nicht hallo")
        def underTest = new EntityFieldWrapper(entity, mode)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)
        when:
        underTest.updateSingleField(aha.get("password"), scimUser.password, "password")
        then:
        scimUser.password == entity.password
    }

    def "should ignore null fields"() {
        given:
        def scimUser = new User.Builder("test").setPassword("a").build()
        def entity = new UserEntity()
        def underTest = new EntityFieldWrapper(entity, mode)
        when:
        underTest.updateSimpleField(null, scimUser)
        then:
        notThrown(NullPointerException)
    }

    def "should not update a empty password of an entity"() {
        given:
        def scimUser = new User.Builder("test")
                .setPassword("")
                .build()

        def entity = new UserEntity()
        entity.setPassword("nicht hallo")
        def underTest = new EntityFieldWrapper(entity, mode)
        def aha = new GetFieldsOfInputAndTarget().getFieldsAsNormalizedMap(UserEntity)
        when:
        underTest.updateSingleField(aha.get("password"), scimUser.password, "password")
        then:
        entity.password == "nicht hallo"
    }
}
