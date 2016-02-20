package org.osiam.resources.controller

import org.osiam.resources.exception.RestExceptionHandler
import org.osiam.resources.helper.AttributesRemovalHelper
import org.osiam.resources.provisioning.SCIMGroupProvisioning
import org.osiam.resources.scim.Group
import org.osiam.resources.scim.SCIMSearchResult
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class GroupControllerSpec extends Specification {

    def scimGroupProvisioning = Mock(SCIMGroupProvisioning)

    def groupController = new GroupController(scimGroupProvisioning, new AttributesRemovalHelper())

    def mockMvc = MockMvcBuilders.standaloneSetup(groupController)
            .setControllerAdvice(new RestExceptionHandler()).build()

    @Shared
    def uuid = UUID.randomUUID()

    @Shared
    def responseGroup = new Group.Builder([displayName: 'Test Group', id: uuid]).build()

    @Shared
    def minimalGroup = '{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:Group"],' +
            ' "displayName": "Test Group"}'

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

    def 'Replacing an invalid group using PUT raises a 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(put("/Groups/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:Group"],"displayName": ""}}'))
        then:
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.status').value(HttpStatus.BAD_REQUEST.toString()))
    }

    def 'Replacing a group using PATCH sets the location header as expected'() {
        when:
        def response = mockMvc.perform(patch("/Groups/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(minimalGroup))

        then:
        1 * scimGroupProvisioning.update(uuid.toString(), _) >> responseGroup
        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(header().string('Location', "http://localhost/Groups/${uuid}"))
                .andExpect(jsonPath('$.id').value(uuid as String)) // is nonsense because it's what we give in
    }

    def 'Replacing an invalid group using PATCH raises a 400 BAD REQUEST'() {
        when:
        def response = mockMvc.perform(put("/Groups/${uuid}")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"schemas": ["urn:ietf:params:scim:schemas:core:2.0:Group"],"displayName": ""}}'))
        then:
        response.andExpect(status().isBadRequest())
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

}
