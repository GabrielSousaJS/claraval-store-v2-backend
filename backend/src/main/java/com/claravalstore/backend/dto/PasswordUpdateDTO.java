package com.claravalstore.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordUpdateDTO {

    @NotBlank(message = "Campo obrigatório")
    private String oldPassword;

    @NotBlank(message = "Campo obrigatório")
    private String newPassword;

}
