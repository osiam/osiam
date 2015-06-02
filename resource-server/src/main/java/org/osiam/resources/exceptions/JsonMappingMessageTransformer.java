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

public class JsonMappingMessageTransformer implements ErrorMessageTransformer {

    // Can not deserialize instance of java.util.ArrayList out of VALUE_STRING token
    // at [Source: java.io.StringReader@5c96bfda; line: 1, column: 2] (through reference chain:
    // org.osiam.resources.scim.User["ims"])
    // will be transformed to
    // Can not deserialize instance of java.util.ArrayList out of VALUE_STRING
    private static final Pattern PATTERN = Pattern.compile(
            "(Can not deserialize instance of [\\w\\.]+ out of \\w+) token.*",
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