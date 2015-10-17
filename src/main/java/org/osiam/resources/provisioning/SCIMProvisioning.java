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

package org.osiam.resources.provisioning;

import org.osiam.resources.scim.Resource;
import org.osiam.resources.scim.SCIMSearchResult;

public interface SCIMProvisioning<T extends Resource> {
    /**
     * This method returns a SCIM resource, it is used for GET calls.
     * <p/>
     * <p/>
     * It must throw an ResourceNotFoundException if no resource got found.
     *
     * @param id
     *            the external identifier of an resource
     * @return the found resource
     * @throws org.osiam.resources.exception.ResourceNotFoundException
     *             if no resource with the given id got found
     */
    T getById(String id);

    /**
     * This method creates a resource, it is used for POST calls.
     *
     * @param resource
     *            A resource representation which should be created
     * @return the created resource representation
     * @throws org.osiam.resources.exception.ResourceExistsException
     *             if the resource already exists
     */
    T create(T resource);

    /**
     * This method replaces an resource, it is used for PUT calls.
     *
     * @param id
     *            , the external identifier of an resource
     * @param resource
     *            , an resource representation which should be created
     * @return the updated resource
     * @throws org.osiam.resources.exception.ResourceNotFoundException
     *             if no resource with the given id got found
     */
    T replace(String id, T resource);

    /**
     * This method updates an resource, it is used for PATCH calls.
     *
     * @param id
     *            , the external identifier of an resource
     * @param resource
     *            , an resource representation which should be created
     * @return the updated resource
     * @throws org.osiam.resources.exception.ResourceNotFoundException
     *             if no resource with the given id got found
     */
    T update(String id, T resource);

    /**
     * This method deletes an user found by its id, it is used for DELETE calls.
     *
     * @param id
     *            the identifier of the user.
     */
    void delete(String id);

    /**
     * This method provide a search across users or groups.
     *
     *
     * @param filter
     *            the filter expression.
     * @param sortBy
     *            the field name which is used to sort by
     * @param sortOrder
     *            the sort order. Allowed: "ascending" and "descending". Default is "ascending"
     * @param count
     *            the maximum returned results per page. Default: 100
     * @param startIndex
     *            the value to start from for paging. Default: 1
     * @return the search results
     */
    SCIMSearchResult<T> search(String filter, String sortBy, String sortOrder, int count, int startIndex);

}
