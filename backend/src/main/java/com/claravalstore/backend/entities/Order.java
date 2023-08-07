package com.claravalstore.backend.entities;

import com.claravalstore.backend.entities.enums.OrderStatus;
import com.claravalstore.backend.entities.enums.converters.OrderStatusConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;

    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    @Getter @Setter
    private Instant moment;

    @Getter @Setter
    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @Getter @Setter
    private User client;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    @Getter @Setter
    private Payment payment;

    @OneToMany(mappedBy = "id.order")
    @Getter
    private Set<OrderItem> items = new HashSet<>();

    public Order() {
    }

    public Order(Long id, Instant moment, OrderStatus status, User client) {
        this.id = id;
        this.moment = moment;
        this.status = status;
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
