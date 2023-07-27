package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.Address;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

public class AddressDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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

    public AddressDTO() {
    }

    public AddressDTO(Long id, String street, String cep, Integer number, String neighborhood, String complement, String city, String state, String country) {
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

    public AddressDTO(Address entity) {
        id = entity.getId();
        street = entity.getStreet();
        cep = entity.getCep();
        number = entity.getNumber();
        neighborhood = entity.getNeighborhood();
        complement = entity.getComplement();
        city = entity.getCity();
        state = entity.getState();
        country = entity.getCountry();
    }
}
