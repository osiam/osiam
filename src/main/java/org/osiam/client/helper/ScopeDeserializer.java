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
package org.osiam.client.helper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.osiam.client.oauth.Scope;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ScopeDeserializer extends JsonDeserializer<Set<Scope>> {

    @Override
    public Set<Scope> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String text = jp.getText();
        return parseParameterList(text);
    }

    private Set<Scope> parseParameterList(String values) {
        Set<Scope> scopeResult = new HashSet<>();
        if (values != null && values.trim().length() > 0) {
            String[] scopes = values.split("\\s+");
            for (String scope : scopes) {
                scopeResult.add(new Scope(scope));
            }
        }
        return scopeResult;
    }
}
