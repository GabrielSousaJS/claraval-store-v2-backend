package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Order;
import com.claravalstore.backend.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@DataJpaTest
class OrderRepositoryTests {

    @Autowired
    private OrderRepository repository;

    private long countTotalOrders;

    @BeforeEach
    void setUp() {
        countTotalOrders = 2L;
    }

    @Test
    void findAllShouldReturnPageOfOrder() {
        Pageable pageable = PageRequest.of(0, 12);

        Page<Order> result = repository.findAll(pageable);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getTotalElements());
    }

    @Test
    void findAllByClientIdShouldReturnListOfOrder() {
        Long clientId = 1L;

        List<Order> result = repository.findAllByClientId(clientId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Order order = Factory.createOrder();
        order.setId(null);

        order = repository.save(order);

        Assertions.assertNotNull(order.getId());
        Assertions.assertEquals(countTotalOrders + 1L, order.getId());
    }
}
