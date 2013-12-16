package org.osiam.resources.exceptions;

public class OsiamException extends RuntimeException {

    private static final long serialVersionUID = -292158452140136468L;

    public OsiamException() {
        super();
    }

    public OsiamException(String message, Throwable cause) {
        super(message, cause);
    }

    public OsiamException(String message) {
        super(message);
    }

}
