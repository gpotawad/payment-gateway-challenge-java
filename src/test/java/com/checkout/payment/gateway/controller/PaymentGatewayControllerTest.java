package com.checkout.payment.gateway.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankServiceException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;

import java.util.UUID;

import com.checkout.payment.gateway.service.PaymentGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    PaymentGatewayService paymentGatewayService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getPaymentById_whenIdIsValid_thenCorrectResponseIsReturned() throws Exception {
        PaymentResponse expectedResponse = new PaymentResponse();
        expectedResponse.setId(UUID.randomUUID());
        expectedResponse.setAmount(10);
        expectedResponse.setCurrency("USD");
        expectedResponse.setStatus(PaymentStatus.AUTHORIZED);
        expectedResponse.setExpiryMonth(12);
        expectedResponse.setExpiryYear(2024);
        expectedResponse.setCardNumberLastFour(4321);

        when(paymentGatewayService.getPaymentById(any())).thenReturn(expectedResponse);

        mvc.perform(MockMvcRequestBuilders.get("/payment/" + expectedResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus().getName()))
                .andExpect(jsonPath("$.cardNumberLastFour").value(expectedResponse.getCardNumberLastFour()))
                .andExpect(jsonPath("$.expiryMonth").value(expectedResponse.getExpiryMonth()))
                .andExpect(jsonPath("$.expiryYear").value(expectedResponse.getExpiryYear()))
                .andExpect(jsonPath("$.currency").value(expectedResponse.getCurrency()))
                .andExpect(jsonPath("$.amount").value(expectedResponse.getAmount()));
    }

    @Test
    void getPaymentById_whenIdIsInvalid_thenBadRequestIsReturned() throws Exception {
        when(paymentGatewayService.getPaymentById(any())).thenThrow(new EventProcessingException("Invalid ID"));
        mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Invalid ID"));
    }

    @Test
    void processPayment_whenRequestIsValid_thenAuthorisedResponseIsReturned() throws Exception {
        UUID uuid = UUID.randomUUID();

        PaymentRequest request = new PaymentRequest();
        request.setCardNumber("1234567812345678");
        request.setExpiryMonth(12);
        request.setExpiryYear(2025);
        request.setCurrency("USD");
        request.setAmount(100);
        request.setCvv(123);

        PaymentResponse expectedResponse = new PaymentResponse();
        expectedResponse.setId(uuid);
        expectedResponse.setCardNumberLastFour(1234);
        expectedResponse.setExpiryMonth(12);
        expectedResponse.setExpiryYear(2025);
        expectedResponse.setCurrency("USD");
        expectedResponse.setAmount(100);
        expectedResponse.setStatus(PaymentStatus.AUTHORIZED);

        when(paymentGatewayService.processPayment(any(PaymentRequest.class))).thenReturn(expectedResponse);

        mvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.status").value(expectedResponse.getStatus().getName()))
                .andExpect(jsonPath("$.cardNumberLastFour").value(expectedResponse.getCardNumberLastFour()))
                .andExpect(jsonPath("$.expiryMonth").value(expectedResponse.getExpiryMonth()))
                .andExpect(jsonPath("$.expiryYear").value(expectedResponse.getExpiryYear()))
                .andExpect(jsonPath("$.currency").value(expectedResponse.getCurrency()))
                .andExpect(jsonPath("$.amount").value(expectedResponse.getAmount()));
    }

    @Test
    void processPayment_whenPaymentCantBeProcessed_then4xxClientErrorIsReturned() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setCardNumber("1234567812345678");
        request.setExpiryMonth(12);
        request.setExpiryYear(2025);
        request.setCurrency("USD");
        request.setAmount(100);
        request.setCvv(123);

        when(paymentGatewayService.processPayment(any(PaymentRequest.class))).thenThrow(new EventProcessingException("Problem with processing a request"));

        mvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void processPayment_whenBankCantProcess_then5xxServerErrorIsReturned() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setCardNumber("1234567812345678");
        request.setExpiryMonth(12);
        request.setExpiryYear(2025);
        request.setCurrency("USD");
        request.setAmount(100);
        request.setCvv(123);

        when(paymentGatewayService.processPayment(any(PaymentRequest.class))).thenThrow(new BankServiceException());

        mvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void processPayment_whenBankGetsBadRequest_thenBadRequestIsReturned() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setCardNumber("1234567812345678");
        request.setExpiryMonth(12);
        request.setExpiryYear(2025);
        request.setCurrency("USD");
        request.setAmount(100);
        request.setCvv(123);

        when(paymentGatewayService.processPayment(any(PaymentRequest.class))).thenThrow(new BankServiceException(HttpStatus.BAD_REQUEST, new Exception()));

        mvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
