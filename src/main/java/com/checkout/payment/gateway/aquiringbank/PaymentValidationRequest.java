package com.checkout.payment.gateway.aquiringbank;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class PaymentValidationRequest {

    @JsonProperty("card_number")
    private String cardNumber;

    @JsonProperty("expiry_date")
    private String expiryDate;

    private String currency;

    private int amount;

    private String cvv;

    public PaymentValidationRequest() {}

    public PaymentValidationRequest(String cardNumber, String expiryDate, String currency, int amount, String cvv) {
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.currency = currency;
        this.amount = amount;
        this.cvv = cvv;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
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

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentValidationRequest that = (PaymentValidationRequest) o;
        return amount == that.amount &&
                Objects.equals(cardNumber, that.cardNumber) &&
                Objects.equals(expiryDate, that.expiryDate) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(cvv, that.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, expiryDate, currency, amount, cvv);
    }

    @Override
    public String toString() {
        return "PaymentValidationRequest{" +
                "cardNumber='" + cardNumber + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", cvv='" + cvv + '\'' +
                '}';
    }
}
