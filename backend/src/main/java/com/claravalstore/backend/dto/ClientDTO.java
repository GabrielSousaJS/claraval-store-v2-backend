package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.User;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

public class ClientDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private Long id;
    @Getter
    private String name;

    public ClientDTO() {
    }

    public ClientDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
    }
}