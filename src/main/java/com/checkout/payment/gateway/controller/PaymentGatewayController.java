package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.exception.ValidationException;
import com.checkout.payment.gateway.model.ErrorResponse;
import com.checkout.payment.gateway.model.PaymentRequest;
import com.checkout.payment.gateway.model.PaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController("api")
public class PaymentGatewayController {

    private final PaymentGatewayService paymentGatewayService;

    public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
        this.paymentGatewayService = paymentGatewayService;
    }

    @GetMapping("/payment/{id}")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Payment details found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                value = """
                    {
                        "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "status": "Authorized",
                        "cardNumberLastFour": 0,
                        "expiryMonth": 0,
                        "expiryYear": 0,
                        "currency": "string",
                        "amount": 0
                    }
                """))),
        @ApiResponse(responseCode = "404", description = "Payment details not found",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                value = """
                    {
                        "message": "Payment ID not found",
                        "errors": null
                    }
                """)))
    })
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
    }

    @PostMapping("/payment")
    @Operation( summary = "Process a payment", description = "Processes a payment request with the provided details",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody( description = "Payment request payload",
            required = true, content = @Content( mediaType = "application/json", schema = @Schema(implementation = PaymentRequest.class),
            examples = {
                @ExampleObject( name = "Payment Accepted By Bank",
                    value = """
                        {
                            "currency": "GBP",
                            "amount": 100,
                            "cvv": 123,
                            "card_number": "2222405343248877",
                            "expiry_month": 4,
                            "expiry_year": 2025
                        }
                    """),
                @ExampleObject( name = "Payment Rejected By Bank",
                     value = """
                        {
                            "currency": "USD",
                            "amount": 60000,
                            "cvv": 456,
                            "card_number": "2222405343248112",
                            "expiry_month": 1,
                            "expiry_year": 2026
                        }
                     """)
            })))
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest, BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException(result.getAllErrors());
        }
        if (!paymentRequest.isValidExpiry()) {
            throw new ValidationException("Card is Expired");
        }
        if (!paymentRequest.isValidCurrency()) {
            throw new ValidationException("Currency is not supported, supported values are: USD, EUR, GBP");
        }
        return new ResponseEntity<>(paymentGatewayService.processPayment(paymentRequest), HttpStatus.OK);
    }
}
