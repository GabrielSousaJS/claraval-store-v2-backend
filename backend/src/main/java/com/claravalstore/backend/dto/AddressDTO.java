package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

public class AddressDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String street;

    @Pattern(regexp = "^\\d{5}(?:[-\\s]?\\d{3})?$", message = "Código postal inválido")
    @Getter @Setter
    private String cep;

    @Positive(message = "Informe um valor positivo")
    @Getter @Setter
    private Integer number;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String neighborhood;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String complement;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String city;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String state;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String country;

    public AddressDTO() {
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
