package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.OrderItemDTO;
import com.claravalstore.backend.entities.OrderItem;
import com.claravalstore.backend.entities.pk.OrderItemPk;
import com.claravalstore.backend.repositories.OrderItemRepository;
import com.claravalstore.backend.repositories.OrderRepository;
import com.claravalstore.backend.repositories.ProductRepository;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import com.claravalstore.backend.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class OrderItemServiceTests {

    @InjectMocks
    private OrderItemService service;

    @Mock
    private OrderItemRepository repository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    private long existingId;
    private long nonExistingId;

    private OrderItemPk orderItemPk;
    private OrderItemDTO orderItemDTO;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;

        orderItemPk = Factory.createOrderItemPk();
        orderItemDTO = Factory.createOrderItemDTO();

        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(Factory.createOrderItem());

        Mockito.when(orderRepository.getReferenceById(existingId)).thenReturn(Factory.createOrder());
        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(Factory.createProduct());

        Mockito.when(orderRepository.existsById(existingId)).thenReturn(true);
        Mockito.when(productRepository.existsById(existingId)).thenReturn(true);

        Mockito.when(orderRepository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(productRepository.existsById(nonExistingId)).thenReturn(false);

    }

    @Test
    void saveItemShouldReturnOrderItemWhenIdExists() {
        OrderItem result = service.saveItem(existingId, orderItemDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingId, result.getOrder().getId());
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId, existingId);
        });

        Mockito.verify(repository, Mockito.times(1)).deleteById(orderItemPk);
    }

    @Test
    void deleteShouldResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId, nonExistingId);
        });
    }
}
