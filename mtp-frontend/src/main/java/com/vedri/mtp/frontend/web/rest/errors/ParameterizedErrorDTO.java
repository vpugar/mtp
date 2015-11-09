package com.vedri.mtp.frontend.web.rest.errors;

import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for sending a parameterized error message.
 */
@Getter
public class ParameterizedErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String message;
    private final String[] params;

    public ParameterizedErrorDTO(String message, String... params) {
        this.message = message;
        this.params = params;
    }

}
