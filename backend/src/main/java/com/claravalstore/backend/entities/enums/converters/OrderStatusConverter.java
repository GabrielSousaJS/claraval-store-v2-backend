package com.claravalstore.backend.entities.enums.converters;

import com.claravalstore.backend.entities.enums.OrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return null;
        }

        return orderStatus.getValue();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        return Stream.of(OrderStatus.values())
                .filter(status -> status.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid argument"));
    }
}
