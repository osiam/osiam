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

package org.osiam.storage.entities

import spock.lang.Specification

class ClientEntitySpec extends Specification {
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
        b.getGrants() == under_test.getGrants()
        b.getGrants() == [
            "authorization_code",
            "refresh-token"] as Set
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

    def "should not throw null pointer exception if expiry is null"() {
        given:
        under_test.setExpiry(null)

        when:
        def result = under_test.getExpiry()

        then:
        notThrown(NullPointerException)
        result == null
    }

    def "should generate a secret"() {
        when:
        def b = new ClientEntity()
        then:
        b.clientSecret
        b.clientSecret != under_test.clientSecret
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
        client.setScope(["scope1", "scope2"] as Set)
        client.setImplicit(true)
        client.setValidityInSeconds(123)
        client.setGrants([
            "client_credentials",
            "password"] as Set)

        when:
        def clientEntity = new ClientEntity(client)

        then:
        clientEntity.getId() == "id"
        clientEntity.getClientSecret() == "secret"
        clientEntity.getAccessTokenValiditySeconds() == 200
        clientEntity.getRefreshTokenValiditySeconds() == 200
        clientEntity.getRedirectUri() == "uri"
        clientEntity.getScope() == ["scope1", "scope2"] as Set
        clientEntity.isImplicit()
        clientEntity.getValidityInSeconds() == 123
        clientEntity.getGrants() == [
            "client_credentials",
            "password"] as Set
    }

    def "parametrized constructor should work with null id and secret"() {
        given:
        def client = new ClientEntity()
        client.setId(null)
        client.setClientSecret(null)
        client.setAccessTokenValiditySeconds(200)
        client.setRefreshTokenValiditySeconds(200)
        client.setRedirectUri("uri")
        client.setScope(["scope1", "scope2"] as Set)
        client.setImplicit(true)
        client.setValidityInSeconds(123)
        client.setGrants([
            "client_credentials",
            "password"] as Set)

        when:
        def clientEntity = new ClientEntity(client)

        then:
        clientEntity.getId() == null
        clientEntity.getClientSecret() != null
        clientEntity.getAccessTokenValiditySeconds() == 200
        clientEntity.getRefreshTokenValiditySeconds() == 200
        clientEntity.getRedirectUri() == "uri"
        clientEntity.getScope() == ["scope1", "scope2"] as Set
        clientEntity.isImplicit()
        clientEntity.getValidityInSeconds() == 123
        clientEntity.getGrants() == [
            "client_credentials",
            "password"] as Set
    }

    def "parametrized constructor should work with empty list of grants and generate the default"() {
        given:
        def client = new ClientEntity()
        client.setId("id")
        client.setClientSecret("secret")
        client.setAccessTokenValiditySeconds(200)
        client.setRefreshTokenValiditySeconds(200)
        client.setRedirectUri("uri")
        client.setScope(["scope1", "scope2"] as Set)
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
        clientEntity.getScope() == ["scope1", "scope2"] as Set
        clientEntity.isImplicit()
        clientEntity.getValidityInSeconds() == 123
        clientEntity.getGrants() == [
            "authorization_code",
            "refresh-token"] as Set
    }
}
