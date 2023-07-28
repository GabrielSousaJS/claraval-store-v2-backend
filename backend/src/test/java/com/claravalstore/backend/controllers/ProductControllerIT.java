package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.ProductDTO;
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
class ProductControllerIT {

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
    private Long countTotalProducts;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        adminUsername = "gabriela.oliveira@gmail.com";
        adminPassword = "@Senha123!";

        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 48L;

        productDTO = Factory.createProductDTO();
    }

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        mockMvc.perform(get("/api/products?page=0&size=12")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(countTotalProducts));
    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/api/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    void findyByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/products/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void insertShouldReturnProductDTOCreatedWhenAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(productDTO.getName()));
    }

    @Test
    void insertShouldReturn401WhenNoAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(post("/api/products")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateShouldReturnProductDTOWhenIdExistsAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/api/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value(productDTO.getName()));
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/api/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturn401WhenIdExistsAndNoAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(put("/api/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(delete("/api/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(delete("/api/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn401WhenIdExistsAndNoAdminLogged() throws Exception {
        mockMvc.perform(delete("/api/products/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
