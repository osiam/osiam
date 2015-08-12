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

package org.osiam.security.authentication

import org.osiam.auth.oauth_client.ClientDao
import org.osiam.auth.oauth_client.ClientEntity
import org.osiam.client.oauth.Scope

import spock.lang.Specification

class OsiamClientDetailsServiceSpec extends Specification {

    ClientDao clientDao = Mock()
    OsiamClientDetailsService osiamClientDetailsService = new OsiamClientDetailsService(clientDao: clientDao)
    def clientId = 'client-id'

    def 'loading client details returns a correct converted OsiamClientDetails instance'() {
        given:
        ClientEntity clientEntity = createFullClientEntity(clientId)

        when:
        OsiamClientDetails result = osiamClientDetailsService.loadClientByClientId(clientId)

        then:
        1 * clientDao.getClient(clientId) >> clientEntity
        isEqual(result, clientEntity)
    }

    void isEqual(OsiamClientDetails result, ClientEntity clientEntity) {
        assert result.getId() == clientEntity.getId()
        assert result.getClientSecret() == clientEntity.getClientSecret()
        assert result.getScope() == clientEntity.getScope()
        assert result.getGrants() == clientEntity.getGrants()
        assert result.getRedirectUri() == clientEntity.getRedirectUri()
        assert result.getAccessTokenValiditySeconds() == clientEntity.getAccessTokenValiditySeconds()
        assert result.getRefreshTokenValiditySeconds() == clientEntity.getRefreshTokenValiditySeconds()
        assert result.isImplicit() == clientEntity.isImplicit()
        assert result.getValidityInSeconds() == clientEntity.getValidityInSeconds()
    }

    ClientEntity createFullClientEntity(clientId){
        ClientEntity result = new ClientEntity()
        result.setId(clientId)
        result.setClientSecret('secret')
        result.setScope([Scope.ADMIN] as Set)
        result.setGrants(['grant'] as Set)
        result.setRedirectUri('redirect-uri')
        result.setAccessTokenValiditySeconds(10000)
        result.setRefreshTokenValiditySeconds(100000)
        result.setImplicit(false)
        result.setValidityInSeconds(1000)
        return result
    }
}
