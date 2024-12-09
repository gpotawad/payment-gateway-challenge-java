package com.checkout.payment.gateway.tools;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Since this was not mentioned in a requirements, not using this at the moment
 * These rate limiters are necessary with payment requests
 */

public class RateLimiter {
    private static final int MAX_REQUEST = 2;
    private static final long timeWindow = 60000;
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimiters;

    public RateLimiter(ConcurrentHashMap<String, RateLimitInfo> rateLimiters) {
        this.rateLimiters = rateLimiters;
    }

    public ConcurrentHashMap<String, RateLimitInfo> getRateLimiters() {
        return rateLimiters;
    }

    public boolean allowRequest(String callerId) {
        long currentTime = System.currentTimeMillis();
        if (rateLimiters.containsKey(callerId)) {
            RateLimitInfo info = rateLimiters.get(callerId);
            if (currentTime - info.requestTime > timeWindow) {
                info.numberOfRequests = 1;
                info.requestTime = currentTime;
                rateLimiters.put(callerId, info);
                return true;
            } else if (info.numberOfRequests < MAX_REQUEST) {
                info.numberOfRequests++;
                rateLimiters.put(callerId, info);
                return true;
            } else {
                return false;
            }
        } else {
            rateLimiters.put(callerId, new RateLimitInfo(1, currentTime));
        }
        return true;
    }

}

class RateLimitInfo {
    int numberOfRequests;
    long requestTime;

    public RateLimitInfo(int numberOfRequests, long requestTime) {
        this.numberOfRequests = numberOfRequests;
        this.requestTime = requestTime;
    }
}
