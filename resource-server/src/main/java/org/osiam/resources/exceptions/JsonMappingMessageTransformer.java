package org.osiam.resources.exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: jtodea
 * Date: 28.06.13
 * Time: 16:58
 * To change this template use File | Settings | File Templates.
 */
public class JsonMappingMessageTransformer implements ErrorMessageTransformer {

    //Can not deserialize instance of java.util.ArrayList out of VALUE_STRING token
    //at [Source: java.io.StringReader@5c96bfda; line: 1, column: 2] (through reference chain: org.osiam.resources.scim.User["ims"])
    //will be transformed to
    // Can not deserialize instance of java.util.ArrayList out of VALUE_STRING
    private static final Pattern PATTERN = Pattern.compile("(Can not deserialize instance of [\\w\\.]+ out of \\w+) token.*",
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
