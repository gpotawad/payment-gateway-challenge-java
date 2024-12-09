package com.checkout.payment.gateway.aquiringbank.impl;

import com.checkout.payment.gateway.aquiringbank.BankService;
import com.checkout.payment.gateway.aquiringbank.BankRequest;
import com.checkout.payment.gateway.aquiringbank.BankResponse;
import com.checkout.payment.gateway.exception.BankServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static com.checkout.payment.gateway.constants.CommonConstants.BANK_PAYMENT_FAILURE;

@Service
public class BankServiceImpl implements BankService {
    private static final Logger LOG = LoggerFactory.getLogger(BankServiceImpl.class);

    private final RestTemplate restTemplate;
    private final String paymentValidationUrl;

    @Autowired
    public BankServiceImpl(RestTemplate restTemplate, @Value("${bank.api.payment-validation-url}") String paymentValidationUrl) {
        this.restTemplate = restTemplate;
        this.paymentValidationUrl = paymentValidationUrl;
    }

    private BankResponse callBankAPI(BankRequest bankRequest) {
        return restTemplate.postForObject(paymentValidationUrl, bankRequest, BankResponse.class);
    }

    @Override
    public BankResponse processPayment(BankRequest bankRequest) {
        try {
            return callBankAPI(bankRequest);
        } catch (HttpClientErrorException e) {
            LOG.error(BANK_PAYMENT_FAILURE, e);
            throw new BankServiceException(HttpStatus.valueOf(e.getStatusCode().value()), e);
        } catch (ResourceAccessException e) {
            //  retry logic. Requirements were not stated to have resilience hence leaving it as is
            LOG.error(BANK_PAYMENT_FAILURE, e);
            throw new BankServiceException(HttpStatus.BAD_GATEWAY, e);
        } catch (Exception e) {
            LOG.error(BANK_PAYMENT_FAILURE, e);
            throw new BankServiceException(HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }
}
