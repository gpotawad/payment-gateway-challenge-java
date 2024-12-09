package com.checkout.payment.gateway.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RateLimiterTest {

    private RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiter(new ConcurrentHashMap<>());
    }

    @Test
    void allowRequest_whenFreshRequest_shouldReturnTrue() {
        String callerId = "123";
        boolean actual = rateLimiter.allowRequest(callerId);
        assertTrue(actual);
    }

    @Test
    void allowRequest_whenMoreThan2In1Minutes_shouldReturnFalse() {
        String callerId = "123";
        rateLimiter.allowRequest(callerId);
        rateLimiter.allowRequest(callerId);
        boolean actual = rateLimiter.allowRequest(callerId);
        assertFalse(actual);
    }

    @Test
    void allowRequest_whenMoreThan2InMore1Minutes_shouldReturnTrue(){
        String callerId = "123";
        rateLimiter.getRateLimiters().put(callerId, new RateLimitInfo(2, System.currentTimeMillis() - 61000));
        boolean actual = rateLimiter.allowRequest(callerId);
        assertTrue(actual);
    }

}
