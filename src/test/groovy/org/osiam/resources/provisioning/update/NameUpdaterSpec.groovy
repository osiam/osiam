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

package org.osiam.resources.provisioning.update

import org.osiam.resources.scim.Name
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.UserEntity
import spock.lang.Specification
import spock.lang.Unroll

class NameUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'

    NameUpdater nameUpdater = new NameUpdater()

    def 'removing the name attribute is possible'() {
        given:
        def userEntity = new UserEntity(name: new NameEntity())

        when:
        nameUpdater.update(null, userEntity, ['name'] as Set)

        then:
        userEntity.name == null
    }

    def 'updating the name if at least one sub-attribute is set and the user entity has no name set, creates a new name entity'() {
        given:
        def userEntity = new UserEntity()
        def name = new Name.Builder(formatted: IRRELEVANT).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        userEntity.name.formatted == IRRELEVANT
    }

    def 'deleting the formatted attribute is possible'() {
        given:
        def nameEntity = new NameEntity()
        def userEntity = new UserEntity(name: nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.formatted'] as Set)

        then:
        nameEntity.formatted == null
    }

    def 'updating the formatted attribute is possible'() {
        given:
        def nameEntity = new NameEntity()
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(formatted: IRRELEVANT).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.formatted == IRRELEVANT
    }

    @Unroll
    def 'updating formatted attribute to #value is not possible'() {
        given:
        def nameEntity = new NameEntity(formatted: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(formatted: formatted).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.formatted != value

        where:
        value          | formatted
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the familyName attribute is possible'() {
        given:
        def nameEntity = new NameEntity(familyName: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.familyName'] as Set)

        then:
        nameEntity.familyName == null
    }

    def 'updating the familyName attribute is possible'() {
        given:
        def nameEntity = new NameEntity()
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(familyName: IRRELEVANT).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.familyName == IRRELEVANT
    }

    @Unroll
    def 'updating familyName attribute to #value is not possible'() {
        given:
        def nameEntity = new NameEntity(familyName: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(familyName: familyName).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.familyName == IRRELEVANT

        where:
        value          | familyName
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the givenName attribute is possible'() {
        given:
        def nameEntity = new NameEntity(givenName: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.givenName'] as Set)

        then:
        nameEntity.givenName == null
    }

    def 'updating the givenName attribute is possible'() {
        given:
        def nameEntity = new NameEntity()
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(givenName: IRRELEVANT).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.givenName == IRRELEVANT
    }

    @Unroll
    def 'updating givenName attribute to #value is not possible'() {
        given:
        def nameEntity = new NameEntity(givenName: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(givenName: givenName).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.givenName == IRRELEVANT

        where:
        value          | givenName
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the middleName attribute is possible'() {
        given:
        def nameEntity = new NameEntity(middleName: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.middleName'] as Set)

        then:
        nameEntity.middleName == null
    }

    def 'updating the middleName attribute is possible'() {
        given:
        def nameEntity = new NameEntity()
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(middleName: IRRELEVANT).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.middleName == IRRELEVANT
    }

    @Unroll
    def 'updating middleName attribute to #value is not possible'() {
        given:
        def nameEntity = new NameEntity(middleName: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(middleName: middleName).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.middleName == IRRELEVANT

        where:
        value          | middleName
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the honorificPrefix attribute is possible'() {
        given:
        def nameEntity = new NameEntity(honorificPrefix: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.honorificPrefix'] as Set)

        then:
        nameEntity.honorificPrefix == null
    }

    def 'updating the honorificPrefix attribute is possible'() {
        given:
        def nameEntity = new NameEntity()
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(honorificPrefix: IRRELEVANT).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.honorificPrefix == IRRELEVANT
    }

    @Unroll
    def 'updating honorificPrefix attribute to #value is not possible'() {
        given:
        def nameEntity = new NameEntity(honorificPrefix: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(honorificPrefix: honorificPrefix).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.honorificPrefix == IRRELEVANT

        where:
        value          | honorificPrefix
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the honorificSuffix attribute is possible'() {
        given:
        def nameEntity = new NameEntity(honorificSuffix: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.honorificSuffix'] as Set)

        then:
        nameEntity.honorificSuffix == null
    }

    def 'updating the honorificSuffix attribute is possible'() {
        given:
        def nameEntity = new NameEntity()
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(honorificSuffix: IRRELEVANT).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.honorificSuffix == IRRELEVANT
    }

    @Unroll
    def 'updating honorificSuffix attribute to #value is not possible'() {
        given:
        def nameEntity = new NameEntity(honorificSuffix: IRRELEVANT)
        def userEntity = new UserEntity(name: nameEntity)
        def name = new Name.Builder(honorificSuffix: honorificSuffix).build()

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        nameEntity.honorificSuffix == IRRELEVANT

        where:
        value          | honorificSuffix
        'null'         | null
        'empty string' | ''
    }
}
