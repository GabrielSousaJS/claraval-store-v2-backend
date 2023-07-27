package com.claravalstore.backend.controllers.exceptions;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {

    @Getter
    private List<FieldMessage> errors = new ArrayList<>();

    public void addError(String fieldMessage, String message) {
        errors.add(new FieldMessage(fieldMessage, message));
    }
}
