package com.vedri.mtp.frontend.web.rest.errors;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for transfering error message with a list of field errors.
 */
@Getter
public class ErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final String description;

    private List<FieldErrorDTO> fieldErrors;

    ErrorDTO(String message) {
        this(message, null);
    }

    ErrorDTO(String message, String description) {
        this.message = message;
        this.description = description;
    }

    ErrorDTO(String message, String description, List<FieldErrorDTO> fieldErrors) {
        this.message = message;
        this.description = description;
        this.fieldErrors = fieldErrors;
    }

    public void add(String objectName, String field, String message) {
        if (fieldErrors == null) {
            fieldErrors = new ArrayList<>();
        }
        fieldErrors.add(new FieldErrorDTO(objectName, field, message));
    }

    public List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }
}
