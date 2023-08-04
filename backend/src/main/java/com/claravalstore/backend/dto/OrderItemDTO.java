package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.OrderItem;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

public class OrderItemDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private Long productId;
    @Getter
    private String name;
    @Getter
    private Double price;
    @Getter
    private Integer quantity;
    @Getter
    private String imgUrl;

    public OrderItemDTO() {
    }

    public OrderItemDTO(OrderItem entity) {
        productId = entity.getProduct().getId();
        name = entity.getProduct().getName();
        price = entity.getPrice();
        quantity = entity.getQuantity();
        imgUrl = entity.getProduct().getImgUrl();
    }

}
