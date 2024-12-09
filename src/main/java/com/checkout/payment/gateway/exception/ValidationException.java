package com.checkout.payment.gateway.exception;

import org.springframework.validation.ObjectError;

import java.util.Collections;
import java.util.List;

public class ValidationException extends IllegalArgumentException {
    private final List<ObjectError> errors;

    public ValidationException(List<ObjectError> errors) {
        super("Validation Error");
        this.errors = errors;
    }

    public ValidationException(String error) {
        super("Validation Error");
        this.errors = Collections.singletonList(new ObjectError("invalid_data", error));
    }

    public List<String> getErrors() {
        return errors.stream().map(ObjectError::getDefaultMessage).toList();
    }
}
