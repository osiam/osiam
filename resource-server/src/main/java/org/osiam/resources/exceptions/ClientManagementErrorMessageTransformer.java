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

/**
 * This class wrapping the client management exceptions for existing client id and redirect uri
 * to more human readable output.
 */
public class ClientManagementErrorMessageTransformer implements ErrorMessageTransformer {
    //FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »osiam_client_id_key«\n
    //Detail: Schlüssel »(id)=(test)« existiert bereits.; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »osiam_client_id_key«\n
    //Detail: Schlüssel »(id)=(test)« existiert bereits.

    //Should match upper message and extract the client id value
    private static final Pattern PATTERN_ID = Pattern.compile(".*nested exception is org.hibernate.exception.ConstraintViolationException:.*»\\(id\\)=\\((\\w+)\\)«.*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

    //FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »osiam_client_redirect_uri_key«\n
    //Detail: Schlüssel »(redirect_uri)=(http://localhost:5000/stuff)« existiert bereits.; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: FEHLER: doppelter Schlüsselwert verletzt Unique-Constraint »osiam_client_redirect_uri_key«\n
    //Detail: Schlüssel »(redirect_uri)=(http://localhost:5000/stuff)« existiert bereits.

    //Should match upper message and extract the client redirect uri value
    private static final Pattern PATTERN_URI = Pattern.compile(".*nested exception is org.hibernate.exception.ConstraintViolationException:.*»\\(redirect_uri\\)=\\((.+)\\)«.*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);

    @Override
    public String transform(String message) {
        if (message == null) {
            return null;
        }
        Matcher matcherId = PATTERN_ID.matcher(message);
        if (matcherId.matches()) {
            return "The client with the Id " + matcherId.group(1) + " already exists.";
        }
        Matcher matcherUri = PATTERN_URI.matcher(message);
        if (matcherUri.matches()) {
            return "Another client already defines the redirect URI " + matcherUri.group(1);
        }
        return message;
    }
}
