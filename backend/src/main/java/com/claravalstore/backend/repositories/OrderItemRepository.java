package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.OrderItem;
import com.claravalstore.backend.entities.pk.OrderItemPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemPk> {
}
