package com.claravalstore.backend.controllers;

import com.claravalstore.backend.dto.OrderDTO;
import com.claravalstore.backend.dto.OrderItemDTO;
import com.claravalstore.backend.dto.PaymentDTO;
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
class OrderControllerIT {

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
    private Long productId;
    private Long countTotalOrders;
    private OrderDTO orderDTO;
    private OrderItemDTO orderItemDTO;
    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        adminUsername = "gabriela.oliveira@gmail.com";
        adminPassword = "@Senha123!";

        existingId = 3L;
        nonExistingId = 1000L;
        productId = 1L;
        countTotalOrders = 3L;

        orderDTO = Factory.createOrderDTO();
        orderItemDTO = Factory.createOrderItemDTO();
        paymentDTO = Factory.createPaymentDTO();
    }

    @Test
    void findAllPagedShouldReturnPage() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(get("/api/orders/all-orders?page=0&size=12")
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(countTotalOrders));
    }

    @Test
    void findAllPagedShouldReturn401WhenNoAdminLogged() throws Exception {
        mockMvc.perform(get("/api/orders/all-orders?page=0&size=12")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findAllByClientIdShouldReturnListOfOrderDTOWhenUserLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(get("/api/orders/client-orders")
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].client").exists())
                .andExpect(jsonPath("$[0].payment").exists())
                .andExpect(jsonPath("$[0].items").exists());
    }

    @Test
    void findAllByClientIdShouldReturn401WhenNoUserLogged() throws Exception {
        mockMvc.perform(get("/api/orders/client-orders")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void insertShouldReturnOrderDTOCreatedWhenUserLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.items").exists());
    }

    @Test
    void insertShouldReturn401WhenNoUserLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        mockMvc.perform(post("/api/orders")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addItemToOrderShouldReturnOrderDTOWhenUserLoggedAndIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(orderItemDTO);

        mockMvc.perform(put("/api/orders/add-item/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.items").exists());
    }

    @Test
    void addItemToOrderShouldNotFoundWhenUserLoggedAndIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        mockMvc.perform(put("/api/orders/add-item/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addItemToOrderShouldReturn401WhenNoUserLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(orderDTO);

        mockMvc.perform(put("/api/orders/add-item/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addPaymentShouldReturnOrderDTOWhenUserLoggedAndIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(paymentDTO);

        mockMvc.perform(put("/api/orders/payment/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.payment").exists())
                .andExpect(jsonPath("$.items").exists());
    }

    @Test
    void addPaymentShouldNotFoundWhenUserLoggedAndIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
        String jsonBody = objectMapper.writeValueAsString(paymentDTO);

        mockMvc.perform(put("/api/orders/payment/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addPaymentShouldReturn401WhenNoUserLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(paymentDTO);

        mockMvc.perform(put("/api/orders/payment/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateOrderStatusShouldReturnOrderDTOWhenAdminLoggedAndIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(put("/api/orders/{id}/status?orderStatus=ENVIADO", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.client").exists())
                .andExpect(jsonPath("$.status").value("ENVIADO"))
                .andExpect(jsonPath("$.items").exists());
    }

    @Test
    void updateOrderStatusShouldReturnNotFoundWhenAdminLoggedAndIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(put("/api/orders/{id}/status?orderStatus=ENVIADO", nonExistingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatusShouldReturn401WhenNoAdminLogged() throws Exception {
        mockMvc.perform(put("/api/orders/{id}/status?orderStatus=ENVIADO", existingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteItemShouldReturnNoContentWhenUserLoggedAndIdExists() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(delete("/api/orders/{orderId}/{productId}", existingId, productId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteItemShouldReturnNotFoundWhenUserLoggedAndIdDoesNotExist() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);

        mockMvc.perform(delete("/api/orders/{orderId}/{productId}", nonExistingId, productId)
                        .header("Authorization", "Bearer " + accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItemShouldReturn401WhenNoAdminLogged() throws Exception {
        mockMvc.perform(delete("/api/orders/{orderId}/{productId}", existingId, productId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
