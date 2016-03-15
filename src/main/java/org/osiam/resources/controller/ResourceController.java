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

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.osiam.resources.scim.Resource;
import org.osiam.resources.scim.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public abstract class ResourceController<T extends Resource> {

    protected ResponseEntity<T> buildResponseWithLocation(T resource,
                                                          UriComponentsBuilder builder,
                                                          HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        String resourceType = resource.getSchemas().contains(User.SCHEMA) ? "Users" : "Groups";
        URI location = builder.path("/{resource}/{id}").buildAndExpand(resourceType, resource.getId()).toUri();
        headers.setLocation(location);
        return new ResponseEntity<>(resource, headers, status);
    }

    protected Set<String> extractAttributes(String attributesParameter) {
        Set<String> result = new HashSet<>();
        if (!Strings.isNullOrEmpty(attributesParameter)) {
            result = Sets.newHashSet(
                    Splitter.on(CharMatcher.anyOf(".,"))
                            .trimResults()
                            .omitEmptyStrings()
                            .split(attributesParameter)
            );
        }
        result.add("schemas");
        result.add("id");
        return result;
    }
}
