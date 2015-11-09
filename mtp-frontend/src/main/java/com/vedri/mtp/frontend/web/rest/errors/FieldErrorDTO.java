package com.vedri.mtp.frontend.web.rest.errors;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class FieldErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String objectName;

    private final String field;

    private final String message;

    FieldErrorDTO(String dto, String field, String message) {
        this.objectName = dto;
        this.field = field;
        this.message = message;
    }

}
