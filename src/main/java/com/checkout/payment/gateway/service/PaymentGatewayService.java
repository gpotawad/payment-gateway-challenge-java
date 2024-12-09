package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;

import java.util.UUID;

import org.springframework.stereotype.Service;

/**
 * PaymentGatewayService is responsible to
 * 1) Process a payment request and forward it to bank service and create a response base on bank's response
 * 2) Get a payment information of past payments by providing payment id
 * Note: Not using any unique identifier / Idempotency key to validate request of same id
 * Note: Not added any Queue or Map for failed requests
 * processPayment can be Async in realtime scenario
 */
@Service
public interface PaymentGatewayService {

    PaymentResponse getPaymentById(UUID id);

    PaymentResponse processPayment(PaymentRequest paymentRequest);
}
