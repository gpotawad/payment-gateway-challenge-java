package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.constants.CommonConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDate;

public class PaymentRequest implements Serializable {
    //make sure number does not get stored or print anywhere
    @JsonProperty("card_number")
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{14,19}$", message = "Card number must be between 14-19 digits and contain only numeric characters")
    private transient String cardNumber;

    @JsonProperty("expiry_month")
    @Min(value = 1, message = "Expiry month must be between 1 and 12")
    @Max(value = 12, message = "Expiry month must be between 1 and 12")
    private int expiryMonth;

    @JsonProperty("expiry_year")
    @Min(value = 2024, message = "Expiry year must be greater than or equal to the current year")
    private int expiryYear;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a valid 3-character ISO currency code")
    private String currency;

    @Min(value = 1, message = "Amount must be greater than 0")
    private int amount; // Amount in the minor currency unit otherwise big decimal is a option.

    @NotNull(message = "CVV is required")
    @Min(value = 100, message = "CVV must be at least 3 digits long")
    @Max(value = 9999, message = "CVV must be at most 4 digits long")
    private int cvv;

    public PaymentRequest() {}

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(int expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(int expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    @JsonIgnore
    public String getExpiryDate() {
        return String.format("%02d/%d", expiryMonth, expiryYear);
    }

    @JsonIgnore
    public int getCardNumberLastFour() {
        return Integer.parseInt(cardNumber.substring(cardNumber.length()-4));
    }

    //Validation can be included in annotation based validation or separate validator class
    @JsonIgnore
    public boolean isValidExpiry() {
        LocalDate now = LocalDate.now();
        LocalDate expiryDate = LocalDate.of(expiryYear, expiryMonth, 1);
        return expiryDate.isAfter(now);
    }
    @JsonIgnore
    public boolean isValidCurrency() {
        return CommonConstants.CURRENCIES.contains(currency);
    }

    @Override
    public String toString() {
        return "PostPaymentRequest{" +
                "cardNumberLastFour=" + getCardNumberLastFour() +
                ", expiryMonth=" + expiryMonth +
                ", expiryYear=" + expiryYear +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", cvv=" + cvv +
                '}';
    }
}
