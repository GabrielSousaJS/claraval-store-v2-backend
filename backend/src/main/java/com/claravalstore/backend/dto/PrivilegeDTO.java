package com.claravalstore.backend.dto;

import com.claravalstore.backend.entities.Privilege;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

public class PrivilegeDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    private Long id;
    @Getter @Setter
    private String authority;

    public PrivilegeDTO() {
    }

    public PrivilegeDTO(Privilege entity) {
        id = entity.getId();
        authority = entity.getAuthority();
    }
}
