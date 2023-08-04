package com.claravalstore.backend.repositories;

import com.claravalstore.backend.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
