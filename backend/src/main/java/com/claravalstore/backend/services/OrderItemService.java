package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.OrderItemDTO;
import com.claravalstore.backend.entities.OrderItem;
import com.claravalstore.backend.entities.pk.OrderItemPk;
import com.claravalstore.backend.repositories.OrderItemRepository;
import com.claravalstore.backend.repositories.OrderRepository;
import com.claravalstore.backend.repositories.ProductRepository;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository repository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    protected OrderItem saveItem(Long orderId, OrderItemDTO dto) {
        OrderItem entity = new OrderItem();
        entity.setOrder(orderRepository.getReferenceById(orderId));
        copyDtoToEntity(entity, dto);
        entity = repository.save(entity);
        return entity;
    }

    private void copyDtoToEntity(OrderItem entity, OrderItemDTO dto) {
        entity.setProduct(productRepository.getReferenceById(dto.getProductId()));
        entity.setQuantity(dto.getQuantity());
        entity.setPrice(entity.getPrice());
    }

    protected void delete(Long orderId, Long productId) {
        if (!orderRepository.existsById(orderId) || !productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Item não encontrado");
        }
        try {
            OrderItemPk primaryKey = new OrderItemPk();
            primaryKey.setOrder(orderRepository.getReferenceById(orderId));
            primaryKey.setProduct(productRepository.getReferenceById(productId));
            repository.deleteById(primaryKey);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Erro de integridade referencial");
        }
    }
}
