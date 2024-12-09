package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

    @ExceptionHandler(EventProcessingException.class)
    public ResponseEntity<ErrorResponse> handleProcessingException(EventProcessingException ex) {
        LOG.error("Exception happened", ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        LOG.error("Exception happened", ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), ex.getErrors()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BankServiceException.class)
    public ResponseEntity<ErrorResponse> handleBankServiceException(BankServiceException ex) {
        LOG.error("Exception happened", ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage()), ex.getStatus());
    }
}
