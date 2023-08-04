package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.Order;
import com.claravalstore.backend.entities.OrderItem;
import com.claravalstore.backend.entities.enums.OrderStatus;
import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO {

    @Getter
    private Long id;
    @Getter
    private Instant moment;
    @Getter
    private OrderStatus status;
    @Getter
    private ClientDTO client;
    @Getter
    private PaymentDTO payment;
    @Getter
    private List<OrderItemDTO> items = new ArrayList<>();

    public OrderDTO() {
    }

    public OrderDTO(Order entity) {
        id = entity.getId();
        moment = entity.getMoment();
        status = entity.getStatus();
        client = new ClientDTO(entity.getClient());
        payment = (entity.getPayment() == null) ? null : new PaymentDTO(entity.getPayment());
        for (OrderItem item : entity.getItems()) {
            OrderItemDTO itemDto = new OrderItemDTO(item);
            items.add(itemDto);
        }
    }
}
