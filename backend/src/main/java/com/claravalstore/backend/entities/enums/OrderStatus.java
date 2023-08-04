package com.claravalstore.backend.entities.enums;

import lombok.Getter;

public enum OrderStatus {

    AGUARDANDO_PAGAMENTO("Aguardando pagamento"),
    PAGO("Pago"),
    ENVIADO("Enviado"),
    ENTREGUE("Entregue");

    @Getter
    private String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
