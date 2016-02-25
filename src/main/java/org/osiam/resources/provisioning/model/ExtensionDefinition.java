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
package org.osiam.resources.provisioning.model;

import java.util.HashMap;
import java.util.Map;

import org.osiam.resources.scim.Extension;
import org.osiam.resources.scim.ExtensionFieldType;

/**
 * This class contains meta information about a {@link Extension}.
 */
public class ExtensionDefinition {

    private String urn;

    private Map<String, String> namedTypePairs = new HashMap<>();

    public ExtensionDefinition(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public Map<String, String> getNamedTypePairs() {
        return namedTypePairs;
    }

    public void setNamedTypePairs(Map<String, String> namedTypePairs) {
        this.namedTypePairs = namedTypePairs;
    }

    public void addPair(String fieldName, ExtensionFieldType<?> type) {
        namedTypePairs.put(fieldName, type.getName());
    }
}
