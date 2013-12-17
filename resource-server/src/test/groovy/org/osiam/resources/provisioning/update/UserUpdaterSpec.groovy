package org.osiam.resources.provisioning.update

import org.osiam.resources.exceptions.OsiamException
import org.osiam.resources.scim.Meta
import org.osiam.resources.scim.User
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification
import spock.lang.Unroll

class UserUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'

    NameUpdater nameUpdater = Mock()
    ResourceUpdater resourceUpdater = Mock()
    UserUpdater userUpdater = new UserUpdater(resourceUpdater: resourceUpdater, nameUpdater: nameUpdater)

    User user
    UserEntity userEntity = Mock()

    def 'updating a user triggers resource updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * resourceUpdater.update(user, userEntity)
    }

    def 'removing the userName attribute raises exception'() {
        given:
        Meta meta = new Meta(attributes: ['userName'] as Set)
        user = new User.Builder(meta: meta).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        def oe = thrown(OsiamException)
        oe.getMessage().contains('userName')
    }

    def 'updating the userName is possible' () {
        given:
        User user = new User.Builder(IRRELEVANT).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * userEntity.setUserName(IRRELEVANT)
    }

    @Unroll
    def 'updating userName to #value is not possible'() {
        given:
        User user = new User.Builder(userName: userName).build()

        when:
        userUpdater.update(user, userEntity)

        then:
        0 * userEntity.setUserName(_)

        where:
        value          | userName
        'null'         | null
        'empty string' | ''
    }

    def 'updating a user triggers name updater'() {
        given:
        user = new User.Builder().build()

        when:
        userUpdater.update(user, userEntity)

        then:
        1 * nameUpdater.update(null, userEntity, new HashSet())
    }
}
