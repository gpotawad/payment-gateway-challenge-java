package com.checkout.payment.gateway.aquiringbank.impl;

import com.checkout.payment.gateway.aquiringbank.BankService;
import com.checkout.payment.gateway.aquiringbank.PaymentValidationRequest;
import com.checkout.payment.gateway.aquiringbank.PaymentValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//TODO:  retry, error and Circuit Breaker for Resiliency
//TODO: think if WebClient can be used
//TODO: add check external api is up or down
//TODO: add test
@Service
class BankServiceImpl implements BankService {

    private final RestTemplate restTemplate;
    private final String paymentValidationUrl;

    @Autowired
    public BankServiceImpl(RestTemplate restTemplate, @Value("${bank.api.payment-validation-url}") String paymentValidationUrl) {
        this.restTemplate = restTemplate;
        this.paymentValidationUrl = paymentValidationUrl;
    }

    @Override
    public PaymentValidationResponse validatePayment(PaymentValidationRequest paymentValidationRequest) {
        return restTemplate.postForObject(paymentValidationUrl, paymentValidationRequest, PaymentValidationResponse.class);
    }
}
