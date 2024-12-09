package com.checkout.payment.gateway.aquiringbank;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class BankRequest {

    @JsonProperty("card_number")
    private String cardNumber;

    @JsonProperty("expiry_date")
    private String expiryDate;

    private String currency;

    private int amount;

    private String cvv;

    private UUID key;

    public BankRequest() {}

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

    public UUID getKey() { return this.key; }
    public void setKey(UUID key) { this.key = key; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankRequest that = (BankRequest) o;
        return amount == that.amount &&
                Objects.equals(cardNumber, that.cardNumber) &&
                Objects.equals(expiryDate, that.expiryDate) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(key, that.key) &&
                Objects.equals(cvv, that.cvv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, expiryDate, currency, amount, cvv, key);
    }

    @Override
    public String toString() {
        return "PaymentValidationRequest{" +
                "cardNumber='" + cardNumber + '\'' +
                ", expiryDate='" + expiryDate + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", cvv='" + cvv + '\'' +
                ", key='" + key + '\'' +
                '}';
    }
}
