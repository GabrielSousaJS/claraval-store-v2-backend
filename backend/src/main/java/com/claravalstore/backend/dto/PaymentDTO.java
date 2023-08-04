package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.Payment;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class PaymentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private Long id;
    @Getter
    private Instant moment;

    public PaymentDTO() {
    }

    public PaymentDTO(Payment payment) {
        id = payment.getId();
        moment = payment.getMoment();
    }
}
