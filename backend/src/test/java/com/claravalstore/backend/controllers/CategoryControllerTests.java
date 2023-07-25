package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.CategoryDTO;
import com.claravalstore.backend.services.CategoryService;
import com.claravalstore.backend.services.exceptions.DatabaseException;
import com.claravalstore.backend.services.exceptions.ResourceNotFoundException;
import com.claravalstore.backend.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private CategoryDTO categoryDTO;


    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        categoryDTO = Factory.createCategoryDTO();
        List<CategoryDTO> list = List.of(categoryDTO);

        Mockito.when(service.findAll()).thenReturn(list);

        Mockito.when(service.findById(existingId)).thenReturn(categoryDTO);
        Mockito.when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(service.insert(Mockito.any())).thenReturn(categoryDTO);

        Mockito.when(service.update(Mockito.eq(existingId), Mockito.any())).thenReturn(categoryDTO);
        Mockito.when(service.update(Mockito.eq(nonExistingId), Mockito.any())).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(service).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);
    }

    @Test
    void findAllShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", existingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/categories/{id}", nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertShouldReturnCategoryDTOCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(post("/api/categories")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void updateShouldReturnCategoryDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/api/categories/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void udpateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        mockMvc.perform(put("/api/categories/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", existingId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnBadRequestWhenIdIsDependent() throws Exception {
        mockMvc.perform(delete("/api/categories/{id}", dependentId))
                .andExpect(status().isBadRequest());
    }
}
