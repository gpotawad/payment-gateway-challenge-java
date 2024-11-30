package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.aquiringbank.BankService;
import com.checkout.payment.gateway.aquiringbank.PaymentValidationRequest;
import com.checkout.payment.gateway.aquiringbank.PaymentValidationResponse;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final BankService bankService;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, BankService bankService) {
    this.paymentsRepository = paymentsRepository;
    this.bankService = bankService;
  }

  public PostPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid ID"));
  }

  public UUID processPayment(PostPaymentRequest paymentRequest) {
    return UUID.randomUUID();
  }

  public PostPaymentResponse makePayment(PostPaymentRequest request) {
    PaymentValidationRequest paymentValidationRequest = new PaymentValidationRequest();
    paymentValidationRequest.setAmount(request.getAmount());
    paymentValidationRequest.setCurrency(request.getCurrency());
    paymentValidationRequest.setCardNumber(request.getCardNumber());
    paymentValidationRequest.setExpiryDate(request.getExpiryDate());
    paymentValidationRequest.setCvv(String.valueOf(request.getCvv()));
    System.out.println(paymentValidationRequest);
    PaymentValidationResponse response = bankService.validatePayment(paymentValidationRequest);
    System.out.println(response);
    return new PostPaymentResponse();
  }
}
