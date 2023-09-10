package com.claravalstore.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class NewPasswordDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Campo requerido")
    private String token;

    @NotBlank(message = "Campo requerido")
    @Size(min = 8, message = "Deve ter no mínimo 8 caracteres")
    private String password;
}
