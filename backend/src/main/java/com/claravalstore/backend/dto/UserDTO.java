package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.Address;
import com.claravalstore.backend.entities.User;
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
    @Getter @Setter
    private String name;
    @Getter @Setter
    private Instant birthDate;
    @Getter @Setter
    private String email;

    @Getter @Setter
    private AddressDTO address;

    @Getter
    private List<PrivilegeDTO> privileges = new ArrayList<>();

    public UserDTO() {
    }

    public UserDTO(Long id, String name, Instant birthDate, String email) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
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
