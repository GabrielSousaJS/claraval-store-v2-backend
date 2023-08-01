package com.claravalstore.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "tb_address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String street;
    @Getter @Setter
    private String cep;
    @Getter @Setter
    private Integer number;
    @Getter @Setter
    private String neighborhood;
    @Getter @Setter
    private String complement;
    @Getter @Setter
    private String city;
    @Getter @Setter
    private String state;
    @Getter @Setter
    private String country;

    @OneToOne(mappedBy = "address", cascade = CascadeType.ALL)
    @Getter @Setter
    private User user;

    public Address() {
    }

    public Address(Long id, String street, String cep, Integer number, String neighborhood, String complement, String city, String state, String country) {
        this.id = id;
        this.street = street;
        this.cep = cep;
        this.number = number;
        this.neighborhood = neighborhood;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
