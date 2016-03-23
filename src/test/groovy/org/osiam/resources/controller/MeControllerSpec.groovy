/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.resources.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import org.osiam.resources.exception.RestExceptionHandler
import org.osiam.resources.provisioning.SCIMUserProvisioning
import org.osiam.resources.scim.User
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.hamcrest.core.StringEndsWith.endsWith
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class MeControllerSpec extends Specification {

    def resourceServerTokenServices = Mock(ResourceServerTokenServices)
    def mockMvc

    def accessToken = "IrrelevantAccessToken"

    def userProvisioning = Mock(SCIMUserProvisioning)

    def meController = new MeController(resourceServerTokenServices, userProvisioning)
    OAuth2Authentication authentication = Mock(OAuth2Authentication)
    def id = UUID.randomUUID()
    Authentication userAuthentication = Mock(Authentication)

    def user = new User.Builder(id: id).build()

    def setup() {
        def filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false)
        def objectMapper = new ObjectMapper(filterProvider: filterProvider)
        def jacksonMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper)
        mockMvc = MockMvcBuilders.standaloneSetup(meController)
                .setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(jacksonMessageConverter)
                .build()
        authentication.getUserAuthentication() >> userAuthentication
    }

    def 'should provide logged in user with correct location header set'() {
        when:
        def response = mockMvc.perform(get('/Me')
                .header('Authorization', "Bearer ${accessToken}"))
        then:
        1 * resourceServerTokenServices.loadAuthentication(accessToken) >> authentication
        1 * userAuthentication.getPrincipal() >> user // the user returned by getPrincipal is not fully populated
        1 * userProvisioning.getById(user.id) >> user // But the mocking is irrelevant here.
        response.andExpect(status().isOk())
                .andExpect(header().string('Location', endsWith("/Users/${id}")))
    }

    def 'Using a client-only access token generates a 400 BAD_REQUEST'() {
        when:
        def response = mockMvc.perform(get('/Me')
                .header('Authorization', "Bearer ${accessToken}"))
        then:
        1 * resourceServerTokenServices.loadAuthentication(accessToken) >> authentication
        1 * authentication.isClientOnly() >> true
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.status').value('400'))
    }

    def 'Using an token unknown to OSIAM generates a 400 BAD_REQUEST'() {
        when:
        def response = mockMvc.perform(get('/Me')
                .header('Authorization', "Bearer ${accessToken}"))

        then:
        1 * resourceServerTokenServices.loadAuthentication(accessToken) >> authentication
        1 * userAuthentication.getPrincipal() >> null
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.status').value('400'))
                .andExpect(jsonPath('$.detail').value('User not authenticated.'))
    }
}
