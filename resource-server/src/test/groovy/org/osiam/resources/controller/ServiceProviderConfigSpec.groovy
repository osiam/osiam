package org.osiam.resources.controller

import org.osiam.resources.scim.Constants
import spock.lang.Specification

class ServiceProviderConfigSpec extends Specification {
    def underTest = new ServiceProviderConfigsController()

    def "should return a ServiceProviderConfig"() {
        given:
        def schemas = [Constants.CORE_SCHEMA] as Set

        when:
        def config = underTest.getConfig()

        then:
        config.schemas == schemas
        config.patch.supported
        !config.bulk.supported
        !config.bulk.maxOperations
        !config.bulk.maxPayloadSize
        config.filter.supported
        config.filter.maxResults == 100
        !config.changePassword.supported
        config.sort.supported
        !config.etag.supported
        !config.xmlDataFormat.supported
        config.authenticationSchemes.authenticationSchemes.length == 1
        config.authenticationSchemes.authenticationSchemes[0].name == "Oauth2 Bearer"
        config.authenticationSchemes.authenticationSchemes[0].description
        config.authenticationSchemes.authenticationSchemes[0].specUrl == "http://tools.ietf.org/html/rfc6749"
        config.authenticationSchemes.authenticationSchemes[0].documentationUrl == "http://oauth.net/2/"


    }
}
