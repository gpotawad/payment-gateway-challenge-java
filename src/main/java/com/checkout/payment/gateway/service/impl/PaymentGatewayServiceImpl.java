package com.checkout.payment.gateway.service.impl;

import com.checkout.payment.gateway.aquiringbank.BankService;
import com.checkout.payment.gateway.aquiringbank.BankRequest;
import com.checkout.payment.gateway.aquiringbank.BankResponse;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.BankServiceException;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentGatewayServiceImpl implements PaymentGatewayService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayServiceImpl.class);

    private final PaymentsRepository paymentsRepository;
    private final BankService bankService;

    public PaymentGatewayServiceImpl(PaymentsRepository paymentsRepository, BankService bankService) {
        this.paymentsRepository = paymentsRepository;
        this.bankService = bankService;
    }

    @Override
    public PaymentResponse getPaymentById(UUID id) {
        return paymentsRepository.get(id).orElseThrow(() -> new EventProcessingException("Invalid Payment ID"));
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        BankRequest bankRequest = mapToPaymentValidationRequest(paymentRequest);
        BankResponse bankResponse;

        LOG.info("Processing payment to bank service");

        try {
            bankResponse = bankService.processPayment(bankRequest);
            LOG.info("Payment processed successfully");
            if (bankResponse != null) {
                bankResponse.setKey(bankRequest.getKey());
                return createPaymentInformation(paymentRequest, bankResponse);
            }
        } catch (BankServiceException e) {
            throw new BankServiceException(e.getStatus(), e);
        }

        throw new BankServiceException();
    }

    private PaymentResponse createPaymentInformation(PaymentRequest paymentRequest, BankResponse bankResponse) {
        PaymentResponse paymentResponse = mapToPaymentResponse(paymentRequest, bankResponse);
        try {
            paymentsRepository.add(paymentResponse);
            LOG.info("Payment information saved successfully");
        } catch (Exception e) {
            LOG.info("Payment information can not be saved");
        }
        return paymentResponse;
    }

    private BankRequest mapToPaymentValidationRequest(PaymentRequest paymentRequest) {
        BankRequest bankRequest = new BankRequest();
        //This can be an identity or client key, for now I am generating it random
        bankRequest.setKey(UUID.randomUUID());
        bankRequest.setAmount(paymentRequest.getAmount());
        bankRequest.setCurrency(paymentRequest.getCurrency());
        bankRequest.setCardNumber(paymentRequest.getCardNumber());
        bankRequest.setExpiryDate(paymentRequest.getExpiryDate());
        bankRequest.setCvv(String.valueOf(paymentRequest.getCvv()));
        return bankRequest;
    }

    private PaymentResponse mapToPaymentResponse(PaymentRequest paymentRequest, BankResponse bankResponse) {
        PaymentResponse response = new PaymentResponse();
        response.setStatus(bankResponse.isAuthorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED);
        response.setId(bankResponse.getKey());
        response.setCardNumberLastFour(paymentRequest.getCardNumberLastFour());
        response.setExpiryMonth(paymentRequest.getExpiryMonth());
        response.setExpiryYear(paymentRequest.getExpiryYear());
        response.setCurrency(paymentRequest.getCurrency());
        response.setAmount(paymentRequest.getAmount());
        return response;
    }
}
