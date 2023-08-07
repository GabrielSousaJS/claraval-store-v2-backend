package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.OrderDTO;
import com.claravalstore.backend.dto.OrderItemDTO;
import com.claravalstore.backend.dto.PaymentDTO;
import com.claravalstore.backend.entities.*;
import com.claravalstore.backend.repositories.OrderItemRepository;
import com.claravalstore.backend.repositories.OrderRepository;
import com.claravalstore.backend.repositories.ProductRepository;
import com.claravalstore.backend.services.exceptions.PaymentMadeException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(SpringExtension.class)
class OrderServiceTests {

    @InjectMocks
    private OrderService service;

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthService authService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private OrderItemService orderItemService;

    @Mock
    private ProductService productService;

    private long existingId;
    private long nonExistingId;

    private Order order;
    private OrderDTO orderDTO;

    private OrderItemDTO orderItemDTO;

    private Payment payment;
    private PaymentDTO paymentDTO;


    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;

        order = Factory.createOrder();
        orderDTO = Factory.createOrderDTO();

        payment = Factory.createPayment();
        paymentDTO = Factory.createPaymentDTO();
        orderItemDTO = Factory.createOrderItemDTO();

        OrderItem orderItem = Factory.createOrderItem();
        List<OrderItem> orderItems = List.of(orderItem);
        Set<OrderItem> orderItemSet = Set.of(orderItem);
        PageImpl<Order> page = new PageImpl<>(List.of(order));

        User user = Factory.createUser();
        Product product = Factory.createProduct();

        Mockito.when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.findAllByClientId(ArgumentMatchers.any())).thenReturn(List.of(order));
        Mockito.when(repository.getReferenceById(existingId)).thenReturn(order);
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).getReferenceById(nonExistingId);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(order);

        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(product);

        Mockito.when(orderItemRepository.saveAll(ArgumentMatchers.any())).thenReturn(orderItems);

        Mockito.when(authService.authenticated()).thenReturn(user);

        Mockito.when(paymentService.insert(ArgumentMatchers.any())).thenReturn(payment);

        Mockito.when(orderItemService.saveItem(existingId, orderItemDTO)).thenReturn(orderItem);

        Mockito.doNothing().when(productService).updateQuantityOfProducts(orderItemSet);
    }

    @Test
    void findAllPagedShouldReturnPageOfOrderDTO() {
        Pageable pageable = PageRequest.of(0, 12);

        Page<OrderDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void findAllByClientShouldReturnListOfOrderDTO() {
        List<OrderDTO> result = service.findAllByClient();

        Assertions.assertNotNull(result);
        Assertions.assertFalse(result.isEmpty());
    }

    @Test
    void insertShouldReturnOrderDTOWhenIdIsNull() {
        orderDTO.setId(null);
        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getMoment());
        Assertions.assertNotNull(result.getStatus());
    }

    @Test
    void addItemToOrderShouldReturnOrderDTOWhenIdExists() {
        OrderDTO result = service.addItemToOrder(existingId, orderItemDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getMoment());
        Assertions.assertNotNull(result.getStatus());
        Assertions.assertEquals(orderItemDTO.getProductId() ,result.getItems().get(0).getProductId());
    }

    @Test
    void addItemToOrderShouldThrowPaymentMadeExceptionWhenPaymentIsMade() {
        order.setPayment(payment);
        Assertions.assertThrows(PaymentMadeException.class, () -> {
            service.addItemToOrder(existingId, orderItemDTO);
        });
    }

    @Test
    void addItemToOrderShouldThrowResourceNotFoundExceptionWhenOrderIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.addItemToOrder(nonExistingId, orderItemDTO);
        });
    }

    @Test
    void addPaymentShouldReturnOrderDTOWhenIdExists() {
        OrderDTO result = service.addPayment(existingId, paymentDTO);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getMoment());
        Assertions.assertNotNull(result.getStatus());
        Assertions.assertNotNull(result.getPayment());
    }

    @Test
    void addPaymentShouldThrowPaymentMadeExceptionWhenPaymentIsMade() {
        order.setPayment(payment);
        Assertions.assertThrows(PaymentMadeException.class, () -> {
            service.addPayment(existingId, paymentDTO);
        });
    }

    @Test
    void addPaymentShouldThrowResourceNotFoundExceptionWhenOrderIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.addPayment(nonExistingId, paymentDTO);
        });
    }

    @Test
    void deleteItemShouldDoNothingWhenIdExists() {
        Assertions.assertDoesNotThrow(() -> {
            service.deleteItem(existingId, existingId);
        });
    }

    @Test
    void updateStatusShouldReturnOrderDTOWhenIdExists() {
        OrderDTO result = service.updateStatus(existingId, "ENVIADO");

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getMoment());
        Assertions.assertNotNull(result.getStatus());
    }

    @Test
    void updateStatusShouldThrowResourceNotFoundExceptionWhenOrderIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.updateStatus(nonExistingId, "ENVIADO");
        });
    }
}
