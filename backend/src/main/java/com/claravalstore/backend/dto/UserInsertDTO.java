package com.claravalstore.backend.dto;

import com.claravalstore.backend.services.validation.UserInsertValid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@UserInsertValid
public class UserInsertDTO extends UserDTO {

    @NotBlank(message = "Campo obrigatório")
    @Getter @Setter
    private String password;

    public UserInsertDTO() {
        super();
    }
}
