package org.osiam.auth.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class JsonErrorResult {

    @JsonProperty("error_code")
    private String errorCode;
    private String description;

    public JsonErrorResult(String name, String message) {
        errorCode = name;
        description = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }
}
