package org.osiam.resources.provisioning.update

import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Ignore
import spock.lang.Specification

@Ignore
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

    def 'removin a email is possible'(){
        given:
        MultiValuedAttribute email = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()

        when:
        emailUpdater.update([email] as List, userEntity, [] as Set)

        then:
        true
    }

    def 'adding a new email is possible'(){
        when:
        MultiValuedAttribute email = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        emailUpdater.update([email] as List, userEntity, [] as Set)

        then:
        1 * userEntity.setEmails(_)
    }
}
