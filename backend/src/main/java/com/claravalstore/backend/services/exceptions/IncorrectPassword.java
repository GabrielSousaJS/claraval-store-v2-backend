package com.claravalstore.backend.services.exceptions;

import java.io.Serial;

public class IncorrectPassword extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public IncorrectPassword(String msg) {
        super(msg);
    }

}
