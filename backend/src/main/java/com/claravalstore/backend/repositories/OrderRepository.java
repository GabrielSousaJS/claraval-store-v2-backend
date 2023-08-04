package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT obj FROM Order obj INNER JOIN obj.client client " +
            "WHERE client.id = :clientId")
    List<Order> findAllByClientId(Long clientId);

}
