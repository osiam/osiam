package org.osiam.resources.provisioning.update

import org.osiam.resources.exceptions.OsiamException
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.Name
import org.osiam.resources.scim.User
import org.osiam.storage.entities.NameEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification
import spock.lang.Unroll

class NameUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'

    Name name
    UserEntity userEntity = Mock()
    NameEntity nameEntity = Mock()
    NameUpdater nameUpdater = new NameUpdater()

    def 'removing the name attribute is possible'() {
        when:
        nameUpdater.update(null, userEntity, ['name'] as Set)

        then:
        1 * userEntity.setName(null)
    }

    def 'updating the name if at least one sub-attribute is set and the user entity has no name set, creates a new name entity'(){
        given:
        userEntity = new UserEntity()
        name = new Name(formatted : IRRELEVANT)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        userEntity.getName() != null
    }

    def 'deleting the formatted attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.formatted'] as Set)

        then:
        1 * nameEntity.setFormatted(null)
    }

    def 'updating the formatted attribute is possible'(){
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(formatted : IRRELEVANT)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        1 * nameEntity.setFormatted(IRRELEVANT)
    }

    @Unroll
    def 'updating formatted attribute to #value is not possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(formatted : formatted)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        0 * nameEntity.setFormatted(_)

        where:
        value          | formatted
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the familyName attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.familyName'] as Set)

        then:
        1 * nameEntity.setFamilyName(null)
    }

    def 'updating the familyName attribute is possible'(){
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(familyName : IRRELEVANT)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        1 * nameEntity.setFamilyName(IRRELEVANT)
    }

    @Unroll
    def 'updating familyName attribute to #value is not possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(familyName : familyName)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        0 * nameEntity.setFamilyName(_)

        where:
        value          | familyName
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the givenName attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.givenName'] as Set)

        then:
        1 * nameEntity.setGivenName(null)
    }

    def 'updating the givenName attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(givenName : IRRELEVANT)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        1 * nameEntity.setGivenName(IRRELEVANT)
    }

    @Unroll
    def 'updating givenName attribute to #value is not possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(givenName : givenName)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        0 * nameEntity.setGivenName(_)

        where:
        value          | givenName
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the middleName attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.middleName'] as Set)

        then:
        1 * nameEntity.setMiddleName(null)
    }

    def 'updating the middleName attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(middleName : IRRELEVANT)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        1 * nameEntity.setMiddleName(IRRELEVANT)
    }

    @Unroll
    def 'updating middleName attribute to #value is not possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(middleName : middleName)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        0 * nameEntity.setMiddleName(_)

        where:
        value          | middleName
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the honorificPrefix attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.honorificPrefix'] as Set)

        then:
        1 * nameEntity.setHonorificPrefix(null)
    }

    def 'updating the honorificPrefix attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(honorificPrefix : IRRELEVANT)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        1 * nameEntity.setHonorificPrefix(IRRELEVANT)
    }

    @Unroll
    def 'updating honorificPrefix attribute to #value is not possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(honorificPrefix : honorificPrefix)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        0 * nameEntity.setHonorificPrefix(_)

        where:
        value          | honorificPrefix
        'null'         | null
        'empty string' | ''
    }

    def 'deleting the honorificSuffix attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)

        when:
        nameUpdater.update(null, userEntity, ['name.honorificSuffix'] as Set)

        then:
        1 * nameEntity.setHonorificSuffix(null)
    }

    def 'updating the honorificSuffix attribute is possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(honorificSuffix : IRRELEVANT)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        1 * nameEntity.setHonorificSuffix(IRRELEVANT)
    }

    @Unroll
    def 'updating honorificSuffix attribute to #value is not possible'() {
        given:
        userEntity = new UserEntity(name:nameEntity)
        name = new Name(honorificSuffix : honorificSuffix)

        when:
        nameUpdater.update(name, userEntity, [] as Set)

        then:
        0 * nameEntity.setHonorificSuffix(_)

        where:
        value          | honorificSuffix
        'null'         | null
        'empty string' | ''
    }
}
