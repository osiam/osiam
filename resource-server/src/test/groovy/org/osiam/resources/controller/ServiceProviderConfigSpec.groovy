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

package org.osiam.resources.controller

import spock.lang.Specification

class ServiceProviderConfigSpec extends Specification {
    def underTest = new ServiceProviderConfigsController()

    def "should return a ServiceProviderConfig"() {
        given:
        def schemas = [
                ServiceProviderConfigsController.ServiceProviderConfig.SCHEMA] as Set

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
