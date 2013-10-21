package org.osiam.security.authentication;


public class AuthenticationError {

    private String error;
    private String message;

    public AuthenticationError() {
        // needed
    }

    public AuthenticationError(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
