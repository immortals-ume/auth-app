package com.immortals.authapp.service;


import com.immortals.authapp.model.TokenBucket;
import com.immortals.authapp.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.immortals.authapp.constants.AuthAppConstant.MAX_TOKENS;
import static com.immortals.authapp.constants.AuthAppConstant.REFILL_TOKENS_PER_SECONDS;


@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimiterService {

    private final CacheService<String, TokenBucket> cacheService;

    // TTL for cache entries (optional, e.g. 1 hour)
    private final Duration cacheTtl = Duration.ofHours(1);

    public boolean isAllowed(String ipAddress) {
        long now = System.currentTimeMillis();

        TokenBucket bucket = cacheService.get(ipAddress,ipAddress);

        if (bucket == null) {
            // Initialize new bucket with max tokens
            bucket = new TokenBucket(MAX_TOKENS, now);
        }

        // Calculate tokens to add based on elapsed time
        long elapsedMillis = now - bucket.getLastRefillTimestamp();
        int tokensToAdd = (int) (elapsedMillis / 1000.0 * REFILL_TOKENS_PER_SECONDS);
        int newTokenCount = Math.min(bucket.getTokens() + tokensToAdd, MAX_TOKENS);

        if (newTokenCount > 0) {
            // Consume a token and update bucket
            bucket.setTokens(newTokenCount - 1);
            bucket.setLastRefillTimestamp(now);

            // Update cache with TTL
            cacheService.put(ipAddress, bucket, cacheTtl,ipAddress);
            return Boolean.TRUE;
        } else {
            // No tokens available
            bucket.setTokens(newTokenCount);
            bucket.setLastRefillTimestamp(now);
            cacheService.put(ipAddress, bucket, cacheTtl,ipAddress);
            return Boolean.FALSE;
        }
    }
}
