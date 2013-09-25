package org.osiam.storage.entities

import spock.lang.Specification

class ClientEntityTest extends Specification {
    def under_test = new ClientEntity()

    def "should set id"() {
        when:
        under_test.setId("id")
        then:
        under_test.getId() == "id"
    }

    def "should deliver the same grant types"() {
        when:
        def b = new ClientEntity()
        then:
        b.getAuthorizedGrantTypes() == under_test.getAuthorizedGrantTypes()
        b.getAuthorizedGrantTypes() == ["authorization_code", "refresh-token"] as Set
    }

    def "should be able to set access_token length"() {
        when:
        under_test.setAccessTokenValiditySeconds(2342)
        then:
        under_test.getAccessTokenValiditySeconds() == 2342
    }

    def "should be able to set refresh token validity"() {
        when:
        under_test.setRefreshTokenValiditySeconds(2342)
        then:
        under_test.getRefreshTokenValiditySeconds() == 2342
    }

    def "should be able to set implicit client authorization"() {
        when:
        under_test.setImplicit(true)

        then:
        under_test.isImplicit()
    }

    def "should be able to set the validity in seconds for client approval"() {
        when:
        under_test.setValidityInSeconds(100)

        then:
        under_test.getValidityInSeconds() == 100
    }

    def "should be able to set the day on which approval was granted"() {
        given:
        def date = new Date(1000)

        when:
        under_test.setExpiry(date)

        then:
        under_test.getExpiry() == date
    }

    def "should generate a secret"() {
        when:
        def b = new ClientEntity()
        then:
        b.clientSecret
        b.clientSecret != under_test.clientSecret
    }

    def "resource ids should be empty"() {
        when:
        def ids = under_test.getResourceIds()
        then:
        ids.empty
    }

    def "isSecretRequired should be true"() {
        when:
        def result = under_test.isSecretRequired()
        then:
        result
    }

    def "isScoped should be true"() {
        when:
        def result = under_test.isScoped()
        then:
        result
    }

    def "getRegisteredRedirectUri should return a set which contains redirect_uri"() {
        given:
        under_test.setRedirectUri("should_i_stay_or_should_i_go_now")
        when:
        def result = under_test.getRegisteredRedirectUri()
        then:
        result == [under_test.getRedirectUri()] as Set
    }

    def "getAuthorities should be empty"() {
        when:
        def result = under_test.getAuthorities()
        then:
        result.empty
    }


    def "getAdditionalInformation should be empty"() {
        when:
        def result = under_test.getAdditionalInformation()
        then:
        !result
    }

    def "should be possible to set secret"() {
        given:
        def secret = "moep!"
        when:
        under_test.setClientSecret(secret)
        then:
        under_test.getClientSecret() == secret
    }

    def "should be possible to set scopes"() {
        given:
        def scopes = ['POST', 'GET'] as Set
        when:
        under_test.setScope(scopes)
        then:
        under_test.getScope() == scopes
    }

    def "should be possible to set internal_id"() {
        when:
        under_test.setInternalId(23)
        then:
        under_test.getInternalId() == 23
    }

    def "parametrized constructor should be present for json serializing"() {
        given:
        def client = new ClientEntity()
        client.setId("id")
        client.setClientSecret("secret")
        client.setAccessTokenValiditySeconds(200)
        client.setRefreshTokenValiditySeconds(200)
        client.setRedirectUri("uri")
        client.setScope(["scope1","scope2"] as Set)
        client.setImplicit(true)
        client.setValidityInSeconds(123)
        client.setGrants(["client_credentials", "password"] as Set)

        when:
        def clientEntity = new ClientEntity(client)

        then:
        clientEntity.getId() == "id"
        clientEntity.getClientSecret() == "secret"
        clientEntity.getAccessTokenValiditySeconds() == 200
        clientEntity.getRefreshTokenValiditySeconds() == 200
        clientEntity.getRedirectUri() == "uri"
        clientEntity.getScope() == ["scope1","scope2"] as Set
        clientEntity.isImplicit()
        clientEntity.getValidityInSeconds() == 123
        clientEntity.getAuthorizedGrantTypes() == ["client_credentials", "password"] as Set
    }

    def "parametrized constructor should work with null id and secret"() {
        given:
        def client = new ClientEntity()
        client.setId(null)
        client.setClientSecret(null)
        client.setAccessTokenValiditySeconds(200)
        client.setRefreshTokenValiditySeconds(200)
        client.setRedirectUri("uri")
        client.setScope(["scope1","scope2"] as Set)
        client.setImplicit(true)
        client.setValidityInSeconds(123)
        client.setGrants(["client_credentials", "password"] as Set)

        when:
        def clientEntity = new ClientEntity(client)

        then:
        clientEntity.getId() == null
        clientEntity.getClientSecret() != null
        clientEntity.getAccessTokenValiditySeconds() == 200
        clientEntity.getRefreshTokenValiditySeconds() == 200
        clientEntity.getRedirectUri() == "uri"
        clientEntity.getScope() == ["scope1","scope2"] as Set
        clientEntity.isImplicit()
        clientEntity.getValidityInSeconds() == 123
        clientEntity.getAuthorizedGrantTypes() == ["client_credentials", "password"] as Set
    }

    def "parametrized constructor should work with empty list of grants and generate the default"() {
        given:
        def client = new ClientEntity()
        client.setId("id")
        client.setClientSecret("secret")
        client.setAccessTokenValiditySeconds(200)
        client.setRefreshTokenValiditySeconds(200)
        client.setRedirectUri("uri")
        client.setScope(["scope1","scope2"] as Set)
        client.setImplicit(true)
        client.setValidityInSeconds(123)
        client.setGrants([] as Set)

        when:
        def clientEntity = new ClientEntity(client)

        then:
        clientEntity.getId() == "id"
        clientEntity.getClientSecret() == "secret"
        clientEntity.getAccessTokenValiditySeconds() == 200
        clientEntity.getRefreshTokenValiditySeconds() == 200
        clientEntity.getRedirectUri() == "uri"
        clientEntity.getScope() == ["scope1","scope2"] as Set
        clientEntity.isImplicit()
        clientEntity.getValidityInSeconds() == 123
        clientEntity.getAuthorizedGrantTypes() == ["authorization_code", "refresh-token"] as Set
    }
}
