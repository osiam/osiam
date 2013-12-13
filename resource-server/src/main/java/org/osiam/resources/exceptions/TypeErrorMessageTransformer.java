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
 * This class is used to transform
 * No enum constant org.osiam.storage.entities.*Entity.*.huch
 * Messages to a more readable way.
 */
public class TypeErrorMessageTransformer implements ErrorMessageTransformer {
    // Will load:
    // group 1 = full qualified name of the entity class
    // group 2 = the name of of the entity (without Entity, e.q. PhotoEntity -> Photo)
    // group 3 = the name of the inner enum so that we can combine it to group1+$+group3 (to find the class of the enum)
    // group 4 = the wrong value send by the client
    private static Pattern pattern =
            Pattern.compile("No enum constant (org\\.osiam\\.storage\\.entities\\.(\\w+)Entity)\\.(\\w+).(\\w+)");

    @Override
    public String transform(String message) {
        if (message == null) { return null; }
        Matcher matcher = pattern.matcher(message);
        if (matcher.matches()) {
            String values = loadEnumConstAsStringByClassName(matcher.group(1) + "$" + matcher.group(3)); // NOSONAR - no need to make a constant for numbers
            return matcher.group(4) + " is not a valid " + matcher.group(2) + " type. Only " + values + " are allowed."; // NOSONAR - no need to make a constant for numbers
        }
        return message;
    }

    /**
     * Loads an Enum and return its values combined as String.
     *
     * @param enumName, the full qualified name of the enum
     * @return the string values of the enum
     */
    private String loadEnumConstAsStringByClassName(String enumName) {
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> clazz = (Class<? extends Enum<?>>) Class.forName(enumName);
            Enum<?>[] enumConstants = clazz.getEnumConstants();
            return combineEnumConstantsToString(enumConstants).substring(2);
        } catch (Exception e) {
            return null;
        }

    }

    /**
     * Iterates through a given array of enums and combines each element to a string:
     * , <const.toString>
     *
     * @param enumConstants the array of enums to combine
     * @return combined string list of enums separated by comma
     */
    private String combineEnumConstantsToString(Enum<?>[] enumConstants) {
        StringBuffer buf = new StringBuffer();
        for (Enum<?> e : enumConstants) {
            buf.append(", ");
            buf.append(e.toString());
        }
        return buf.toString();
    }
}