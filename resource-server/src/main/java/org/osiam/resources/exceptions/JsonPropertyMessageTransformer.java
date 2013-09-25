package org.osiam.resources.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 28.06.13
 * Time: 14:56
 * To change this template use File | Settings | File Templates.
 */
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