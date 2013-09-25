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
