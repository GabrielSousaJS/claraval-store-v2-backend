package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.OrderItem;
import com.claravalstore.backend.entities.pk.OrderItemPk;
import com.claravalstore.backend.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
class OrderItemRepositoryTests {

    @Autowired
    private OrderItemRepository repository;

    @Test
    void saveShouldReturnOrderItem() {
        OrderItem orderItem = Factory.createOrderItem();

        orderItem = repository.save(orderItem);

        Assertions.assertNotNull(orderItem);
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {
        OrderItemPk primaryKey = Factory.createOrderItemPk();

        repository.deleteById(primaryKey);
        Optional<OrderItem> result = repository.findById(primaryKey);

        Assertions.assertFalse(result.isPresent());
    }
}
