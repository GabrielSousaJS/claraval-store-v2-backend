package com.claravalstore.backend.services;

import com.claravalstore.backend.dto.OrderDTO;
import com.claravalstore.backend.dto.OrderItemDTO;
import com.claravalstore.backend.dto.PaymentDTO;
import com.claravalstore.backend.entities.Order;
import com.claravalstore.backend.entities.OrderItem;
import com.claravalstore.backend.entities.Product;
import com.claravalstore.backend.entities.User;
import com.claravalstore.backend.entities.enums.OrderStatus;
import com.claravalstore.backend.repositories.OrderItemRepository;
import com.claravalstore.backend.repositories.OrderRepository;
import com.claravalstore.backend.repositories.ProductRepository;
import com.claravalstore.backend.services.exceptions.PaymentMadeException;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AuthService authService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @Transactional(readOnly = true)
    public Page<OrderDTO> findAllPaged(Pageable pageable) {
        Page<Order> page = repository.findAll(pageable);
        return page.map(OrderDTO::new);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findAllByClient() {
        User client = authService.authenticated();
        List<Order> list = repository.findAllByClientId(client.getId());
        return list.stream().map(OrderDTO::new).toList();
    }


    @Transactional
    public OrderDTO insert(OrderDTO dto) {
        Order entity = new Order();
        entity.setMoment(Instant.now());
        entity.setClient(authService.authenticated());

        addItems(entity, dto);

        repository.save(entity);
        orderItemRepository.saveAll(entity.getItems());

        if (dto.getPayment() == null)
            entity.setStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
        else {
            entity.setStatus(OrderStatus.PAGO);
            paymentService.insert(dto.getPayment());
        }

        return new OrderDTO(entity);
    }

    private void addItems(Order entity, OrderDTO dto) {
        entity.getItems().clear();

        for (OrderItemDTO itemDto : dto.getItems()) {
            Product product = productRepository.getReferenceById(itemDto.getProductId());
            OrderItem item = new OrderItem(entity, product, itemDto.getQuantity(), product.getPrice());
            entity.getItems().add(item);
        }
    }

    @Transactional
    public OrderDTO addItemToOrder(Long id, OrderItemDTO dto) {
        try {
            Order entity = repository.getReferenceById(id);

            if (paymentNotMade(entity)) {
                OrderItem item = orderItemService.saveItem(id, dto);
                entity.getItems().add(item);
                repository.save(entity);
                return new OrderDTO(entity);
            } else {
                throw new PaymentMadeException("Não é possível adicionar itens a um pedido já pago");
            }
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Pedido não encontrado");
        }
    }

    @Transactional
    public OrderDTO addPayment(Long id, PaymentDTO dto) {
        try {
            Order entity = repository.getReferenceById(id);

            if (paymentNotMade(entity)) {
                entity.setPayment(paymentService.insert(dto));
                entity.setStatus(OrderStatus.PAGO);
                repository.save(entity);
                productService.updateQuantityOfProducts(entity.getItems());
                return new OrderDTO(entity);
            } else {
                throw new PaymentMadeException("O pagamento da encomenda já foi efetuado");
            }
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Pedido não encontrado");
        }
    }

    private boolean paymentNotMade(Order entity) {
        return entity.getPayment() == null;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteItem(Long orderId, Long productId) {
        orderItemService.delete(orderId, productId);
    }

    @Transactional
    public OrderDTO updateStatus(Long id, String orderStatus) {
        try {
            Order entity = repository.getReferenceById(id);
            entity.setStatus(OrderStatus.valueOf(orderStatus));
            repository.save(entity);
            return new OrderDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Pedido não encontrado");
        }
    }
}
