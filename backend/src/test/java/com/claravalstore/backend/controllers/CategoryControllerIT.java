package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.CategoryDTO;
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
class CategoryControllerIT {

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
    private Long countTotalCategories;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        adminUsername = "gabriela.oliveira@gmail.com";
        adminPassword = "@Senha123!";

        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = 6L;

        categoryDTO = Factory.createCategoryDTO();
    }

    @Test
    void findAllShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(countTotalCategories));
    }

    @Test
    void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void findByIdReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertShouldReturnCategoryDTOCreatedWhenAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(categoryDTO.getName()));
    }

    @Test
    void insertShouldReturn401WhenNoAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(post("/api/categories")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateShouldReturnCategoryDTOWhenIdExistsAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/api/categories/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/api/categories/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void udpateShouldReturn401WhenNoAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/api/categories/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExistsAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(delete("/api/categories/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExistAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(delete("/api/categories/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturn401WhenNoAdminLogged() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", existingId))
                .andExpect(status().isUnauthorized());
    }
}