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

package org.osiam.resources.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonPropertyMessageTransformer implements ErrorMessageTransformer {

    //should only get Unrecognized field and name of the field e.q.:
    //Unrecognized field "extId" (Class org.osiam.resources.scim.User), not marked as ignorable
    //at [Source: java.io.StringReader@1e41ac03; line: 1, column: 11] (through reference chain: org.osiam.resources.scim.User["extId"])
    //will be transformed to
    // Unrecognized field "extId"
    private static final Pattern PATTERN = Pattern.compile("(Unrecognized field \"\\w+\").*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

    @Override
    public String transform(String message) {
        if (message == null) {
            return null;
        }
        Matcher matcher = PATTERN.matcher(message);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return message;
    }
}