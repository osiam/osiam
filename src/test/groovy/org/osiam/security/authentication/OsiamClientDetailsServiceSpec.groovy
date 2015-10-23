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

import org.osiam.auth.oauth_client.ClientEntity
import org.osiam.auth.oauth_client.ClientRepository
import org.springframework.security.oauth2.provider.ClientDetails
import spock.lang.Specification

class OsiamClientDetailsServiceSpec extends Specification {

    ClientRepository clientRepository = Mock()
    OsiamClientDetailsService osiamClientDetailsService =
            new OsiamClientDetailsService(clientRepository: clientRepository)

    def 'loading client details returns a ClientDetails view of a ClientEntity'() {
        given:
        ClientEntity clientEntity = createFullClientEntity('client-id')

        when:
        ClientDetails clientDetails = osiamClientDetailsService.loadClientByClientId('client-id')

        then:
        1 * clientRepository.findById('client-id') >> clientEntity
        clientDetails == clientEntity
    }

    ClientEntity createFullClientEntity(String clientId) {
        ClientEntity result = new ClientEntity()
        result.setClientId(clientId)
        result.setScope(['scope'])
        result.setGrants(['grant'])
        result.setRedirectUri('redirect-uri')
        result.setAccessTokenValiditySeconds(10000)
        result.setRefreshTokenValiditySeconds(100000)
        result.setValidityInSeconds(1000)
        return result
    }
}
