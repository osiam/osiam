package org.osiam.resources.provisioning.update

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification

class EmailUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'

    UserEntity userEntity = Mock()
    EmailEntity emailEntity = Mock()
    EmailUpdater emailUpdater = new EmailUpdater()

    def 'removing all emails is possible'() {
        when:
        emailUpdater.update(null, userEntity, ['emails'] as Set)

        then:
        1 * userEntity.setEmails(null)
    }

    def 'adding a new email is possible'(){
        when:
        MultiValuedAttribute email = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        emailUpdater.update([email] as List, userEntity, ['emails'] as Set)

        then:
        1 * userEntity.setEmails(_)
    }
}
