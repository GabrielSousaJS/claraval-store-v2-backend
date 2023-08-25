package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.AddressDTO;
import com.claravalstore.backend.tests.Factory;
import com.claravalstore.backend.tests.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AddressControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminUsername;
    private String adminPassword;

    private Long existingId;
    private Long nonExistingId;
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        adminUsername = "gabriela.oliveira@gmail.com";
        adminPassword = "@Senha123!";

        existingId = 1L;
        nonExistingId = 1000L;

        addressDTO = Factory.createAddressDTO();
    }

    @Test
    void updateShouldReturnAddressDTOWhenAdminLoggedAndIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(addressDTO);

        mockMvc.perform(put("/api/addresses/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.street").value(addressDTO.getStreet()))
                .andExpect(jsonPath("$.number").value(addressDTO.getNumber()))
                .andExpect(jsonPath("$.complement").value(addressDTO.getComplement()));
    }

    @Test
    void updateShouldReturnNotFoundWhenAdminLoggedAndIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(addressDTO);

        mockMvc.perform(put("/api/addresses/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturn401WhenNoAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(addressDTO);

        mockMvc.perform(put("/api/addresses/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
