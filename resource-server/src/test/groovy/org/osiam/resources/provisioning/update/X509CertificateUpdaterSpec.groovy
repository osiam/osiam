package org.osiam.resources.provisioning.update

import org.osiam.resources.converter.X509CertificateConverter
import org.osiam.resources.scim.MultiValuedAttribute
import org.osiam.storage.entities.X509CertificateEntity
import org.osiam.storage.entities.UserEntity

import spock.lang.Specification


class X509CertificateUpdaterSpec extends Specification {

    static IRRELEVANT = 'irrelevant'
    static IRRELEVANT_02 = 'irrelevant02'

    UserEntity userEntity = Mock()
    X509CertificateEntity x509CertificateEntity = Mock()
    X509CertificateConverter x509CertificateConverter = Mock()
    X509CertificateUpdater x509CertificateUpdater = new X509CertificateUpdater(x509CertificateConverter : x509CertificateConverter)

    def 'removing all x509Certificate is possible'() {
        when:
        x509CertificateUpdater.update(null, userEntity, ['x509Certificates'] as Set)

        then:
        1 * userEntity.removeAllX509Certificates()
        userEntity.getX509Certificates() >> ([
            new X509CertificateEntity(value : IRRELEVANT),
            new X509CertificateEntity(value : IRRELEVANT_02)] as Set)
    }

    def 'removing an x509Certificate is possible'(){
        given:
        MultiValuedAttribute x509Certificate01 = new MultiValuedAttribute.Builder(value : IRRELEVANT, operation : 'delete', ).build()
        X509CertificateEntity x509CertificateEntity01 = new X509CertificateEntity(value : IRRELEVANT)

        when:
        x509CertificateUpdater.update([x509Certificate01] as List, userEntity, [] as Set)

        then:
        1 * x509CertificateConverter.fromScim(x509Certificate01) >> x509CertificateEntity01
        1 * userEntity.removeX509Certificate(x509CertificateEntity01)
    }

    def 'adding a new x509Certificate is possible'(){
        given:
        MultiValuedAttribute x509Certificate = new MultiValuedAttribute.Builder(value : IRRELEVANT).build()
        X509CertificateEntity x509CertificateEntity = new X509CertificateEntity(value : IRRELEVANT)

        when:
        x509CertificateUpdater.update([x509Certificate] as List, userEntity, [] as Set)

        then:
        1 * x509CertificateConverter.fromScim(x509Certificate) >> x509CertificateEntity
        1 * userEntity.addX509Certificate(x509CertificateEntity)
    }

}
