package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.EmailConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.EmailEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class EmailUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    EmailEntity emailEntity = Mock()
    EmailConverter emailConverter = Mock()
    EmailUpdater emailUpdater = new EmailUpdater(emailConverter : emailConverter)

    def 'removing all emails is possible'() {
        when:
        emailUpdater.update(null, userEntity, ['emails'] as Set)

        then:
        1 * userEntity.removeAllEmails()
        userEntity.getEmails() >> ([
            new EmailEntity(value : IRRELEVANT),
            new EmailEntity(value : IRRELEVANT + '2')] as Set)
    }

    def 'removing an email is possible'(){
        given:
        MultiValuedAttribute email01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        EmailEntity emailEntity01 = new EmailEntity(value : IRRELEVANT)

        when:
        emailUpdater.update([email01] as List, userEntity, [] as Set)

        then:
        1 * emailConverter.fromScim(email01) >> emailEntity01
        1 * userEntity.removeEmail(emailEntity01)
    }

    def 'adding a new email is possible'(){
        given:
        MultiValuedAttribute email = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        EmailEntity emailEntity = new EmailEntity(value : IRRELEVANT)

        when:
        emailUpdater.update([email] as List, userEntity, [] as Set)

        then:
        1 * emailConverter.fromScim(email) >> emailEntity
        1 * userEntity.addEmail(emailEntity)
    }

    def 'adding a new primary email removes the primary attribite from the old one'(){
        given:
        MultiValuedAttribute newPrimaryEmail = new MultiValuedAttribute.Builder(value : IRRELEVANT, primary : true).build()
        EmailEntity newPrimaryEmailEntity = new EmailEntity(value : IRRELEVANT, primary : true)

        EmailEntity oldPrimaryEmailEntity = Spy()
        oldPrimaryEmailEntity.setValue(IRRELEVANT_02)
        oldPrimaryEmailEntity.setPrimary(true)

        EmailEntity emailEntity = new EmailEntity(value : IRRELEVANT_02, primary : false)

        when:
        emailUpdater.update([newPrimaryEmail] as List, userEntity, [] as Set)

        then:
        1 * emailConverter.fromScim(newPrimaryEmail) >> newPrimaryEmailEntity
        1 * userEntity.getEmails() >> ([oldPrimaryEmailEntity] as Set)
        1 * userEntity.addEmail(newPrimaryEmailEntity)
        1 * oldPrimaryEmailEntity.setPrimary(false)
    }
}
