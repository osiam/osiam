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
import org.osiam.resources.provisioning.SCIMGroupProvisioning
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.MemberRef
import org.osiam.resources.scim.SCIMSearchResult
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

import static org.osiam.resources.scim.MemberRef.Type.USER
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class GroupControllerSpec extends Specification {

    def scimGroupProvisioning = Mock(SCIMGroupProvisioning)

    def groupController = new GroupController(scimGroupProvisioning)

    def mockMvc

    @Shared
    def uuid = UUID.randomUUID()

    @Shared
    def responseGroup = new Group.Builder([displayName: 'Test Group', id: uuid, externalId: 'externalId'])
            .addMember(new MemberRef.Builder([type: USER]).build())
            .build()

    @Shared
    def minimalGroup = '{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:Group"],' +
            ' "displayName": "Test Group"}'

    def setup() {
        def filterProvider = new SimpleFilterProvider().setFailOnUnknownId(false)
        def objectMapper = new ObjectMapper(filterProvider: filterProvider)
        def jacksonMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper)
        mockMvc = MockMvcBuilders.standaloneSetup(groupController)
                .setControllerAdvice(new RestExceptionHandler())
                .setMessageConverters(jacksonMessageConverter)
                .build()
    }

    def 'Creating a new Group using POST returns correct location'() {
        when:
        def response = mockMvc.perform(post('/Groups')
                .contentType(MediaType.APPLICATION_JSON)
                .content(minimalGroup))

        then:
        1 * scimGroupProvisioning.create(_) >> responseGroup
        response.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(header().string('Location', "http://localhost/Groups/${uuid}"))
                .andExpect(jsonPath('$.id').value(uuid as String)) // is nonsense because it's what we give in
    }

    def 'Filtering a Group created with POST is possible'() {
        when:
        def response = mockMvc.perform(post('/Groups')
                .contentType(MediaType.APPLICATION_JSON)
                .param('attributes', 'externalId')
                .content(minimalGroup))
        then:
        1 * scimGroupProvisioning.create(_) >> responseGroup
        response.andExpect(status().isCreated())
                .andExpect(jsonPath('$.displayName').doesNotExist())
                .andExpect(jsonPath('$.externalId').value('externalId'))
    }

    def 'Creating an invalid group using POST raises a 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(post('/Groups')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:Group"],"displayName": ""}}'))

        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.detail').value('The displayName is mandatory!'))
                .andExpect(jsonPath('$.status').value('400'))
    }

    def 'Creating a Group with invalid Schema raises a 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(post('/Groups')
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["invalid schema"], "displayName": "Test Group"}'))

        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.detail').value('Invalid core schema'))
                .andExpect(jsonPath('$.status').value('400'))
    }

    def 'Retrieving a group calls get on provisioning'() {
        when:
        def response = mockMvc.perform(get("/Groups/${uuid}")
                .accept(MediaType.APPLICATION_JSON))

        then:
        1 * scimGroupProvisioning.getById(uuid.toString()) >> responseGroup
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath('$.id').value(uuid as String)) // is nonsense because it's what we give in
    }

    def 'Filtering on attributes for groups retrieved by id is possible'() {
        when:
        def response = mockMvc.perform(get("/Groups/${uuid}")
                .accept(MediaType.APPLICATION_JSON)
                .param('attributes', 'displayName'))

        then:
        1 * scimGroupProvisioning.getById(uuid.toString()) >> responseGroup
        response.andExpect(jsonPath('$.members').doesNotExist())
                .andExpect(jsonPath('$.displayName').value('Test Group'))
                .andExpect(status().isOk())
    }

    def 'Deleting a group calls delete on provisioning'() {
        when:
        mockMvc.perform(delete("/Groups/${uuid}"))

        then:
        1 * scimGroupProvisioning.delete(uuid.toString())
    }

    def 'Replacing a group using PUT sets the location header correctly'() {
        when:
        def response = mockMvc.perform(put("/Groups/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(minimalGroup))

        then:
        1 * scimGroupProvisioning.replace(uuid.toString(), _) >> responseGroup
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(header().string('Location', "http://localhost/Groups/${uuid}"))
                .andExpect(jsonPath('$.id').value(uuid as String)) // is nonsense because it's what we give in
    }

    def 'Filtering a group replaced by PUT is possible'() {
        when:
        def response = mockMvc.perform(put("/Groups/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .param('attributes', 'externalId')
                .content(minimalGroup))
        then:
        1 * scimGroupProvisioning.replace(uuid.toString(), _) >> responseGroup
        response.andExpect(status().isOk())
                .andExpect(jsonPath('$.displayName').doesNotExist())
                .andExpect(jsonPath('$.externalId').value('externalId'))
    }

    def 'Replacing an invalid group using PUT raises a 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(put("/Groups/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:Group"],"displayName": ""}}'))
        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.status').value(HttpStatus.BAD_REQUEST.toString()))
    }

    def 'Search parameters default to SCIM RFC values'() {
        when:
        def response = mockMvc.perform(get("/Groups")
                .accept(MediaType.APPLICATION_JSON)
                .param('filter', 'irrelevant'))

        then:
        1 * scimGroupProvisioning.search('irrelevant', null, 'ascending', 100, 1) >> new SCIMSearchResult<Group>([], 1, 100, 1)
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    }

    def 'Providing parameters override defaults'() {
        when:
        def response = mockMvc.perform(get("/Groups")
                .accept(MediaType.APPLICATION_JSON)
                .param('filter', 'irrelevant')
                .param('sortBy', 'irrelevant')
                .param('sortOrder', 'descending')
                .param('count', '10000')
                .param('startIndex', '500'))

        then:
        1 * scimGroupProvisioning.search('irrelevant', 'irrelevant', 'descending', 10000, 500) >> new SCIMSearchResult<Group>([], 1, 100, 1)
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
    }

    def 'filtering searches on attributes is possible'() {
        when:
        def response = mockMvc.perform(get("/Groups")
                .param('attributes', 'displayName'))

        then:
        1 * scimGroupProvisioning.search(_, _, _, 100, 1) >> new SCIMSearchResult<Group>([responseGroup], 1, 100, 1)
        response.andExpect(jsonPath('$.Resources[0].members').doesNotExist())
                .andExpect(jsonPath('$.Resources[0].displayName').value('Test Group'))
    }

    def 'id is always returned'() {
        when:
        def response = mockMvc.perform(get("/Groups")
                .accept(MediaType.APPLICATION_JSON)
                .param('attributes', 'displayName'))

        then:
        1 * scimGroupProvisioning.search(_, _, _, _, _) >> new SCIMSearchResult<Group>([responseGroup], 1, 100, 1)
        response.andExpect(jsonPath('$.Resources[0].id').value(uuid as String))
    }
}
