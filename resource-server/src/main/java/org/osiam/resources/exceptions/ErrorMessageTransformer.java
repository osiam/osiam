package org.osiam.resources.exceptions;

/**
 * This interface is to check if error message needs to be transformed in
 *
 * @see{org.osiam.resources.exceptions.HandleException} and transform it to a more readable error message.
 */
public interface ErrorMessageTransformer {

    /**
     * May transforms error message. This method must first check if a message needs to be transformed and if so
     * transform it otherwise it must return the original value.
     *
     * @param message the message to may be transformed
     * @return the original message if the check failed, a transformed message otherwise
     */
    String transform(String message);
}
