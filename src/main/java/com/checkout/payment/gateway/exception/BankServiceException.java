package com.checkout.payment.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class BankServiceException extends RuntimeException {
    private static final Logger LOG = LoggerFactory.getLogger(BankServiceException.class);
    private final HttpStatus status;
    public BankServiceException(HttpStatus status, Exception exception) {
        super(exception.getMessage(), exception);
        this.status = status;
        LOG.error("Bank service failed : ", exception);
    }

    public BankServiceException() {
        super("Something went wrong on bank service");
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
        LOG.error("Bank service failed with an unknown error");
    }

    public HttpStatus getStatus() {
        return status;
    }
}
