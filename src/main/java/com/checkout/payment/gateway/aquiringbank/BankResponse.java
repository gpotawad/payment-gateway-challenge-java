package com.checkout.payment.gateway.aquiringbank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.UUID;

public class BankResponse {
    private boolean authorized;

    @JsonProperty("authorization_code")
    private String authorizationCode;

    @JsonIgnore
    private UUID key;

    public BankResponse() {
    }

    public BankResponse(boolean authorized, String authorizationCode) {
        this.authorized = authorized;
        this.authorizationCode = authorizationCode;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public UUID getKey() {
        return this.key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankResponse that = (BankResponse) o;
        return authorized == that.authorized &&
                Objects.equals(authorizationCode, that.authorizationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authorized, authorizationCode);
    }

    @Override
    public String toString() {
        return "PaymentValidationResponse{" +
                "authorized=" + authorized +
                ", authorizationCode='" + authorizationCode + '\'' +
                '}';
    }
}
