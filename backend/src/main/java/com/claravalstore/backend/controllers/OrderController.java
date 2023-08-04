package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.OrderDTO;
import com.claravalstore.backend.dto.OrderItemDTO;
import com.claravalstore.backend.dto.PaymentDTO;
import com.claravalstore.backend.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/all-orders")
    public ResponseEntity<Page<OrderDTO>> findAllPaged(Pageable pageable) {
        Page<OrderDTO> page = service.findAllPaged(pageable);
        return ResponseEntity.ok(page);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @GetMapping(value = "/client-orders")
    public ResponseEntity<List<OrderDTO>> findAllByClientId() {
        List<OrderDTO> list = service.findAllByClient();
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PostMapping
    public ResponseEntity<OrderDTO> insert(@RequestBody OrderDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping(value = "/add-item/{id}")
    public ResponseEntity<OrderDTO> addItemToOrder(@PathVariable Long id, @RequestBody OrderItemDTO dto) {
        OrderDTO newDto = service.addItemToOrder(id, dto);
        return ResponseEntity.ok().body(newDto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping(value = "/payment/{id}")
    public ResponseEntity<OrderDTO> addPayment(@PathVariable Long id, @RequestBody PaymentDTO dto) {
        OrderDTO newDto = service.addPayment(id, dto);
        return ResponseEntity.ok().body(newDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam(value = "orderStatus", defaultValue = "") String orderStatus
    ) {
        OrderDTO dto = service.updateStatus(id, orderStatus);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @DeleteMapping(value = "/{orderId}/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        service.deleteItem(orderId, itemId);
        return ResponseEntity.noContent().build();
    }
}
