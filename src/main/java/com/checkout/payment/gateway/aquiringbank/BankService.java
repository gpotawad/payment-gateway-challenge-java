package com.checkout.payment.gateway.aquiringbank;

import org.springframework.stereotype.Service;

@Service
public interface BankService {
    PaymentValidationResponse validatePayment(final PaymentValidationRequest paymentValidationRequest);
}
