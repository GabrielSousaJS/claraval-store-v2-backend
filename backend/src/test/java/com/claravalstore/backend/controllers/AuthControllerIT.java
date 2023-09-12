package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.EmailDTO;
import com.claravalstore.backend.dto.NewPasswordDTO;
import com.claravalstore.backend.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private EmailDTO emailDTO;
    private NewPasswordDTO newPasswordDTO;

    @BeforeEach
    void setUp() {
        emailDTO = Factory.createEmailDTO();
        newPasswordDTO = Factory.createNewPasswordDTO();
    }

    @Test
    void createRecoverTokenShouldReturnNoContentWhenValidEmail() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(emailDTO);

        mockMvc.perform(post("/api/auth/recover-token")
                        .content(jsonBody)
                        .contentType("application/json"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createRecoverTokenShouldReturnNotFoundWhenInvalidEmail() throws Exception {
        EmailDTO invalidEmail = new EmailDTO("emailinvalido@gmail.com");
        String jsonBody = objectMapper.writeValueAsString(invalidEmail);

        mockMvc.perform(post("/api/auth/recover-token")
                        .content(jsonBody)
                        .contentType("application/json"))
                .andExpect(status().isNotFound());
    }
}
