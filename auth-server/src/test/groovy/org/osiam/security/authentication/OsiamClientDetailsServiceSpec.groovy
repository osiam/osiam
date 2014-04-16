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

import com.fasterxml.jackson.databind.ObjectMapper
import org.osiam.helper.HttpClientHelper
import org.osiam.helper.HttpClientRequestResult
import org.osiam.resources.ClientSpring
import spock.lang.Specification

class OsiamClientDetailsServiceSpec extends Specification {

    def jacksonMapperMock = Mock(ObjectMapper)
    def httpClientHelperMock = Mock(HttpClientHelper)
    def clientDetailsLoadingBean = new OsiamClientDetailsService(mapper: jacksonMapperMock, httpClientHelper: httpClientHelperMock,
            httpScheme: "http", serverHost: "localhost", serverPort: 8080)

    def "OsiamClientDetailsService should implement springs ClientDetailsService and therefore returning a client found by client ID as ClientSpring representation"() {
        given:
        def resultingClient = "the resulting client as JSON string"
        def clientSpringMock = Mock(ClientSpring)
        def requestResult = new HttpClientRequestResult(resultingClient, 200)

        when:
        def result = clientDetailsLoadingBean.loadClientByClientId("ClientId")

        then:
        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-resource-server/authentication/client/ClientId") >> requestResult
        1 * jacksonMapperMock.readValue(requestResult.body, ClientSpring.class) >> clientSpringMock
        result instanceof ClientSpring
    }

    def "If jackson throws an IOException it should be mapped to an RuntimeException"() {
        given:
        def resultingClient = "the resulting client as JSON string"
        def requestResult = new HttpClientRequestResult(resultingClient, 200)

        when:
        clientDetailsLoadingBean.loadClientByClientId("ClientId")

        then:
        1 * httpClientHelperMock.executeHttpGet("http://localhost:8080/osiam-resource-server/authentication/client/ClientId") >> requestResult
        1 * jacksonMapperMock.readValue(requestResult.body, ClientSpring.class) >> { throw new IOException() }
        thrown(RuntimeException)
    }

    def "An update of the client to set the expiry date should be possible"() {
        given:
        def clientSpringMock = Mock(ClientSpring)
        def oldClientExpiryDate = new Date()

        when:
        clientDetailsLoadingBean.updateClient(clientSpringMock, "ClientId")

        then:
        1 * clientSpringMock.getExpiry() >> oldClientExpiryDate
        1 * httpClientHelperMock.executeHttpPut("http://localhost:8080/osiam-resource-server/authentication/client/ClientId", "expiry", oldClientExpiryDate.toString())
    }
}