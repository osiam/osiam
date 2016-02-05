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

package org.osiam.resources.scim;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

/**
 * A class that holds all information from a search request
 * <p>
 * For more detailed information please look at the <a
 * href="http://tools.ietf.org/html/draft-ietf-scim-core-schema-02">SCIM core schema 2.0</a>
 * </p>
 *
 * @param <T> {@link User} or {@link Group}
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SCIMSearchResult<T> {

    public static final String SCHEMA = "urn:ietf:params:scim:api:messages:2.0:ListResponse";
    private long totalResults;
    private long itemsPerPage;
    private long startIndex;
    private Set<String> schemas = new HashSet<>(Collections.singletonList(SCHEMA));
    private List<T> resources = new ArrayList<>();

    /**
     * Default constructor for Jackson
     */
    SCIMSearchResult() {
    }

    public SCIMSearchResult(List<T> resources, long totalResults, long itemsPerPage, long startIndex) {
        this.resources = resources;
        this.totalResults = totalResults;
        this.itemsPerPage = itemsPerPage;
        this.startIndex = startIndex;
    }


    /**
     * gets a list of found {@link User}s or {@link Group}s
     *
     * @return a list of found resources
     */
    @JsonProperty("Resources")
    public List<T> getResources() {
        return resources;
    }

    /**
     * The total number of results returned by the list or query operation. This may not be equal to the number of
     * elements in the Resources attribute of the list response if pagination is requested.
     *
     * @return the total result
     */
    public long getTotalResults() {
        return totalResults;
    }

    /**
     * Gets the schemas of the search result
     *
     * @return the search result schemas
     */
    public Set<String> getSchemas() {
        return schemas;
    }

    /**
     * The number of Resources returned in a list response page.
     *
     * @return items per page
     */
    public long getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * The 1-based index of the first result in the current set of list results.
     *
     * @return the start index of the actual page
     */
    public long getStartIndex() {
        return startIndex;
    }
}
