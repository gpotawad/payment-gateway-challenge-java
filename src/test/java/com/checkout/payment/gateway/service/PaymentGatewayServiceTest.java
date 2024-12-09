package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.aquiringbank.BankService;
import com.checkout.payment.gateway.aquiringbank.BankRequest;
import com.checkout.payment.gateway.aquiringbank.BankResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankServiceException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.impl.PaymentGatewayServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentGatewayServiceTest {

    private PaymentsRepository paymentsRepository;
    private BankService bankService;
    private PaymentGatewayService paymentGatewayService;

    @BeforeEach
    void setUp() {
        paymentsRepository = mock(PaymentsRepository.class);
        bankService = mock(BankService.class);
        paymentGatewayService = new PaymentGatewayServiceImpl(paymentsRepository, bankService);
    }

    @Test
    void getPaymentById_whenIdIsValid_thenPaymentResponseIsReturned() {
        UUID id = UUID.randomUUID();
        PaymentResponse mockResponse = new PaymentResponse();
        mockResponse.setId(id);

        when(paymentsRepository.get(id)).thenReturn(Optional.of(mockResponse));

        PaymentResponse response = paymentGatewayService.getPaymentById(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        verify(paymentsRepository, times(1)).get(id);
    }

    @Test
    void getPaymentById_whenIdIsInvalid_thenThrowsEventProcessingException() {
        UUID id = UUID.randomUUID();

        when(paymentsRepository.get(id)).thenReturn(Optional.empty());

        assertThrows(EventProcessingException.class, () -> paymentGatewayService.getPaymentById(id));
        verify(paymentsRepository, times(1)).get(id);
    }

    @Test
    void processPayment_whenPaymentIsValid_thenAuthorizedStatusIsReturned() {
        PaymentRequest request = new PaymentRequest();
        request.setCardNumber("1234567812345678");
        request.setExpiryMonth(12);
        request.setExpiryYear(2025);
        request.setCurrency("USD");
        request.setAmount(100);
        request.setCvv(123);

        BankResponse validationResponse = new BankResponse();
        validationResponse.setAuthorized(true);

        when(bankService.processPayment(any(BankRequest.class))).thenReturn(validationResponse);

        PaymentResponse savedResponse = paymentGatewayService.processPayment(request);

        verify(paymentsRepository, times(1)).add(savedResponse);

        assertNotNull(savedResponse);
        assertEquals(5678, savedResponse.getCardNumberLastFour());
        assertEquals(12, savedResponse.getExpiryMonth());
        assertEquals(2025, savedResponse.getExpiryYear());
        assertEquals("USD", savedResponse.getCurrency());
        assertEquals(100, savedResponse.getAmount());
        assertEquals(PaymentStatus.AUTHORIZED, savedResponse.getStatus());
    }

    @Test
    void processPayment_whenPaymentIsValid_thenNotAuthorizedStatusIsReturned() {
        PaymentRequest request = new PaymentRequest();
        request.setCardNumber("1234567812345678");

        BankResponse validationResponse = new BankResponse();
        validationResponse.setAuthorized(false);

        when(bankService.processPayment(any(BankRequest.class))).thenReturn(validationResponse);

        PaymentResponse savedResponse = paymentGatewayService.processPayment(request);

        verify(paymentsRepository, times(1)).add(savedResponse);
        assertNotNull(savedResponse);
        assertEquals(PaymentStatus.DECLINED, savedResponse.getStatus());
    }

    @Test
    void processPayment_whenBankResponseIsNull_thenThrowsBankServiceException() {
        when(bankService.processPayment(any(BankRequest.class))).thenReturn(null);
        assertThrows(BankServiceException.class, () -> paymentGatewayService.processPayment(new PaymentRequest()));
    }

    @Test
    void processPayment_whenBankConnectionIsClosed_thenThrowsBankServiceException() {
        when(bankService.processPayment(any(BankRequest.class))).thenThrow(new BankServiceException());
        assertThrows(BankServiceException.class, () -> paymentGatewayService.processPayment(new PaymentRequest()));
    }
}
