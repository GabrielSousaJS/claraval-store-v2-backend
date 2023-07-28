package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.Address;
import com.claravalstore.backend.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class UserDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private Long id;

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String name;

    @Past(message = "A data de nascimento não pode ser futura")
    @Getter @Setter
    private Instant birthDate;

    @Email(message = "E-mail inválido")
    @Getter @Setter
    private String email;

    @Getter @Setter
    private AddressDTO address;

    @Getter
    private List<PrivilegeDTO> privileges = new ArrayList<>();

    public UserDTO() {
    }

    public UserDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
        birthDate = entity.getBirthDate();
        email = entity.getEmail();
        entity.getPrivileges().forEach(privilege -> this.privileges.add(new PrivilegeDTO(privilege)));
    }

    public UserDTO(User entity, Address address) {
        this(entity);
        this.address = new AddressDTO(address);
    }
}
