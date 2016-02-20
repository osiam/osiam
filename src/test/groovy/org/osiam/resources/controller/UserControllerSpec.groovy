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

import org.osiam.auth.token.TokenService
import org.osiam.resources.exception.RestExceptionHandler
import org.osiam.resources.helper.AttributesRemovalHelper
import org.osiam.resources.provisioning.SCIMUserProvisioning
import org.osiam.resources.scim.SCIMSearchResult
import org.osiam.resources.scim.User
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class UserControllerSpec extends Specification {

    def scimUserProvisioning = Mock(SCIMUserProvisioning)
    def tokenService = Mock(TokenService)

    def UserController = new UserController(scimUserProvisioning, tokenService, new AttributesRemovalHelper())

    def mockMvc = MockMvcBuilders.standaloneSetup(UserController)
            .setControllerAdvice(new RestExceptionHandler()).build()

    @Shared
    def uuid = UUID.randomUUID()

    @Shared
    def responseUser = new User.Builder([displayName: 'Test User', id: uuid]).build()

    @Shared
    def minimalUser = '{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:User"],' +
            ' "userName": "Test User"}'

    def 'Creating a new User using POST returns correct location'() {
        when:
        def response = mockMvc.perform(post('/Users')
                .contentType(MediaType.APPLICATION_JSON)
                .content(minimalUser))

        then:
        1 * scimUserProvisioning.create(_) >> responseUser
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string('Location', "http://localhost/Users/${uuid}"))
                .andExpect(jsonPath('$.id').value(uuid as String)) // is nonsense because it's what we give in
    }

    def 'Creating an user with an invalid multi-value attribute raises 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(post('/Users')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:User"],' +
                ' "userName": "Test User", "emails": [{"primary": "true"}]}'
        ))

        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.status').value('400'))
                .andExpect(jsonPath('$.detail').value('Multi-Valued attributes may not have empty values'))
    }

    def 'Creating an invalid User using POST raises a 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(post('/Users')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:User"],"displayName": ""}}'))

        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.detail').value('The userName is mandatory!'))
                .andExpect(jsonPath('$.status').value('400'))
    }

    def 'Retrieving a User calls get on provisioning'() {
        when:
        def response = mockMvc.perform(get("/Users/${uuid}")
                .accept(MediaType.APPLICATION_JSON))

        then:
        1 * scimUserProvisioning.getById(uuid.toString()) >> responseUser
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath('$.id').value(uuid as String)) // is nonsense because it's what we give in
    }

    def 'Deleting a User removes the users tokens and calls delete on provisioning'() {
        when:
        mockMvc.perform(delete("/Users/${uuid}"))

        then:
        1 * tokenService.revokeAllTokensOfUser(uuid.toString())
        1 * scimUserProvisioning.delete(uuid.toString())
    }

    def 'Replacing a User using PUT sets the location header correctly'() {
        when:
        def response = mockMvc.perform(put("/Users/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(minimalUser))

        then:
        1 * scimUserProvisioning.replace(uuid.toString(), _) >> responseUser
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(header().string('Location', "http://localhost/Users/${uuid}"))
                .andExpect(jsonPath('$.id').value(uuid as String)) // is nonsense because it's what we give in
    }

    def 'Replacing an invalid User using PUT raises a 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(put("/Users/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:User"],"userName": ""}}'))
        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.status').value(HttpStatus.BAD_REQUEST.toString()))
    }

    def 'Replacing a User using PATCH sets the location header as expected'() {
        when:
        def response = mockMvc.perform(patch("/Users/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(minimalUser))

        then:
        1 * scimUserProvisioning.update(uuid.toString(), _) >> responseUser
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(header().string('Location', "http://localhost/Users/${uuid}"))
                .andExpect(jsonPath('$.id').value(uuid as String)) // is nonsense because it's what we give in
    }

    def 'Replacing an invalid User using PATCH raises a 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(put("/Users/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:User"],"userName": ""}}'))
        then:
        response.andExpect(status().isBadRequest())
    }

    def 'Search parameters default to SCIM RFC values'() {
        when:
        def response = mockMvc.perform(get("/Users")
                .accept(MediaType.APPLICATION_JSON)
                .param('filter', 'irrelevant'))

        then:
        1 * scimUserProvisioning.search('irrelevant', null, 'ascending', 100, 1) >> new SCIMSearchResult<User>([], 1, 100, 1)
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    }

    def 'Providing parameters override defaults'() {
        when:
        def response = mockMvc.perform(get("/Users")
                .accept(MediaType.APPLICATION_JSON)
                .param('filter', 'irrelevant')
                .param('sortBy', 'irrelevant')
                .param('sortOrder', 'descending')
                .param('count', '10000')
                .param('startIndex', '500'))

        then:
        1 * scimUserProvisioning.search('irrelevant', 'irrelevant', 'descending', 10000, 500) >> new SCIMSearchResult<User>([], 1, 100, 1)
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    }

}
