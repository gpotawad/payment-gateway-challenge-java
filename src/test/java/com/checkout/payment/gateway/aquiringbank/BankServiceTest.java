package com.checkout.payment.gateway.aquiringbank;

import com.checkout.payment.gateway.aquiringbank.impl.BankServiceImpl;
import com.checkout.payment.gateway.exception.BankServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BankServiceTest {

    private RestTemplate restTemplate;
    private BankService bankService;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        bankService = new BankServiceImpl(restTemplate, "http://localhost:8080/payment");
    }

    @Test
    void testProcessPayment_SuccessfulPayment() {
        BankResponse bankResponse = new BankResponse(true, "success");

        when(restTemplate.postForObject(anyString(), any(BankRequest.class), any())).thenReturn(bankResponse);

        BankResponse actualResponse = bankService.processPayment(new BankRequest());

        assertNotNull(actualResponse);
        assertTrue(actualResponse.isAuthorized());
    }

    @Test
    void testProcessPayment_UnSuccessfulPayment() {
        BankResponse bankResponse = new BankResponse(false, null);

        when(restTemplate.postForObject(anyString(), any(BankRequest.class), any())).thenReturn(bankResponse);

        BankResponse actualResponse = bankService.processPayment(new BankRequest());

        assertNotNull(actualResponse);
        assertFalse(actualResponse.isAuthorized());
    }

    @Test
    void testProcessPayment_ThrowsBadRequest() {
        when(restTemplate.postForObject(anyString(), any(BankRequest.class), any())).thenReturn(new HttpClientErrorException(HttpStatusCode.valueOf(400), "Bad request"));
        assertThrows(BankServiceException.class, () -> bankService.processPayment(new BankRequest()));
    }

    @Test
    void testProcessPayment_ThrowsConnectionClosed() {
        when(restTemplate.postForObject(anyString(), any(BankRequest.class), any())).thenReturn(new ResourceAccessException("Connection is closed"));
        assertThrows(BankServiceException.class, () -> bankService.processPayment(new BankRequest()));
    }

    @Test
    void testProcessPayment_ThrowsAnyException() {
        when(restTemplate.postForObject(anyString(), any(BankRequest.class), any())).thenReturn(new RuntimeException("Unknown error"));
        assertThrows(BankServiceException.class, () -> bankService.processPayment(new BankRequest()));
    }

}
