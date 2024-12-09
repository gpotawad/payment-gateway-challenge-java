package com.checkout.payment.gateway.aquiringbank;

import org.springframework.stereotype.Service;

/**
 * BankService is responsible to
 * Process a payment and call bank's api.
 * Returns a response or flags an exception based of failure type
 */
@Service
public interface BankService {
    BankResponse processPayment(final BankRequest bankRequest);
}
