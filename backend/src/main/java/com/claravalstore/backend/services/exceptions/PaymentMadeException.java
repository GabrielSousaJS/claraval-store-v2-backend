package com.claravalstore.backend.services.exceptions;

import java.io.Serial;

public class PaymentMadeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public PaymentMadeException(String msg) {
        super(msg);
    }
}
