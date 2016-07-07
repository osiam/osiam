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
package org.osiam.resources.controller;

import org.osiam.resources.provisioning.SCIMGroupProvisioning;
import org.osiam.resources.scim.Group;
import org.osiam.resources.scim.SCIMSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

/**
 * HTTP Api for groups. You can create, delete, replace, update and search groups.
 */
@RequestMapping(value = "/Groups")
@Transactional
@RestController
public class GroupController extends ResourceController<Group> {

    private final SCIMGroupProvisioning scimGroupProvisioning;

    @Autowired
    public GroupController(SCIMGroupProvisioning scimGroupProvisioning) {
        this.scimGroupProvisioning = scimGroupProvisioning;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<MappingJacksonValue> create(@RequestBody @Valid Group group,
                                                      @RequestParam(required = false) String attributes,
                                                      UriComponentsBuilder builder) throws IOException {
        Group createdGroup = scimGroupProvisioning.create(group);
        return buildResponseWithLocation(createdGroup, builder, HttpStatus.CREATED, attributes);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public MappingJacksonValue get(@PathVariable final String id,
                                   @RequestParam(required = false) String attributes) {
        Group group = scimGroupProvisioning.getById(id);
        return buildResponse(group, attributes);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable final String id) {
        scimGroupProvisioning.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<MappingJacksonValue> replace(@PathVariable final String id,
                                                       @RequestBody @Valid Group group,
                                                       @RequestParam(required = false) String attributes,
                                                       UriComponentsBuilder builder)
            throws IOException {
        Group updatedGroup = scimGroupProvisioning.replace(id, group);
        return buildResponseWithLocation(updatedGroup, builder, HttpStatus.OK, attributes);
    }

    @RequestMapping(method = RequestMethod.GET)
    public MappingJacksonValue searchWithGet(@RequestParam Map<String, String> requestParameters) {
        return searchWithPost(requestParameters);
    }

    @RequestMapping(value = "/.search", method = RequestMethod.POST)
    public MappingJacksonValue searchWithPost(@RequestParam Map<String, String> requestParameters) {
        SCIMSearchResult<Group> scimSearchResult = scimGroupProvisioning.search(requestParameters.get("filter"),
                requestParameters.get("sortBy"),
                requestParameters.getOrDefault("sortOrder", "ascending"),
                Integer.parseInt(requestParameters.getOrDefault("count", "" + SCIMSearchResult.MAX_RESULTS)),
                Integer.parseInt(requestParameters.getOrDefault("startIndex", "1")));

        String attributes = (requestParameters.containsKey("attributes") ? requestParameters.get("attributes") : "");
        return buildResponse(scimSearchResult, attributes);
    }
}
