package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.PaymentDTO;
import com.claravalstore.backend.entities.Payment;
import com.claravalstore.backend.repositories.OrderRepository;
import com.claravalstore.backend.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    protected Payment insert(PaymentDTO dto) {
        Payment entity = new Payment();
        entity.setMoment(dto.getMoment());
        entity.setOrder(orderRepository.getReferenceById(dto.getId()));
        return repository.save(entity);
    }
}
