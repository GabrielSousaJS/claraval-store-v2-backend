package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.UserInsertDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIT {

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
    private Long countTotalUsers;
    private UserInsertDTO userInsertDTO;

    @BeforeEach
    void setUp() {
        adminUsername = "gabriela.oliveira@gmail.com";
        adminPassword = "@Senha123!";

        existingId = 1L;
        nonExistingId = 1000L;
        countTotalUsers = 5L;

        userInsertDTO = Factory.createUserInsertDTO();
    }

    @Test
    void findAllPagedShouldReturnPageWhenAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(countTotalUsers));
    }

    @Test
    void findAllPagedShouldReturn401WhenNoAdminLogged() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findByIdShouldReturnUserDTOWhenIdExistsWhenAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(get("/api/users", existingId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExistsAndAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(get("/api/users/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByIdShouldReturn401WhenNoAdminLogged() throws Exception {
        mockMvc.perform(get("/api/users/{id}", existingId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void insertClientShouldReturnUserDTOCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        mockMvc.perform(post("/api/users")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(userInsertDTO.getName()));
    }

    @Test
    void insertAdminShouldReturnUserDTOCreatedWhenAdminLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        mockMvc.perform(post("/api/users/add-admin")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(userInsertDTO.getName()));
    }

    @Test
    void insertAdminShouldReturn401WhenNoAdminLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(userInsertDTO);

        mockMvc.perform(post("/api/users/add-admin")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
