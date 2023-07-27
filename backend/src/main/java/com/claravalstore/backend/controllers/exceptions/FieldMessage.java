package com.claravalstore.backend.controllers.exceptions;

import lombok.Getter;
import lombok.Setter;

public class FieldMessage {

    @Getter @Setter
    private String fieldName;
    @Getter @Setter
    private String message;

    public FieldMessage(String fieldError, String message) {
        this.fieldName = fieldError;
        this.message = message;
    }
}
