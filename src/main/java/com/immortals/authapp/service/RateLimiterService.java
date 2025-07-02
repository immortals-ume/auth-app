package com.immortals.authapp.service;

import com.immortals.authapp.model.helper.TokenBucket;

import com.immortals.authapp.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.immortals.authapp.constants.AuthAppConstant.MAX_TOKENS;
import static com.immortals.authapp.constants.AuthAppConstant.REFILL_TOKENS_PER_SECONDS;
import static com.immortals.authapp.constants.CacheConstants.RATE_LIMITING_HASH_KEY;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateLimiterService {

    private final CacheService<String, String, TokenBucket> hashCacheService;
    private final Duration cacheTtl = Duration.ofHours(1);

    public boolean isAllowed(String ipAddress) {
        long now = System.currentTimeMillis();
        TokenBucket bucket = hashCacheService.get(RATE_LIMITING_HASH_KEY, ipAddress, ipAddress);

        if (bucket == null) {
            bucket = new TokenBucket(MAX_TOKENS, now);
        }

        long elapsedMillis = now - bucket.getLastRefillTimestamp();
        int tokensToAdd = (int) (elapsedMillis / 1000.0 * REFILL_TOKENS_PER_SECONDS);
        int newTokenCount = Math.min(bucket.getTokens() + tokensToAdd, MAX_TOKENS);

        if (newTokenCount > 0) {
            bucket.setTokens(newTokenCount - 1);
            bucket.setLastRefillTimestamp(now);
            hashCacheService.put(RATE_LIMITING_HASH_KEY, ipAddress, bucket, cacheTtl, ipAddress);
            return true;
        } else {
            bucket.setTokens(newTokenCount);
            bucket.setLastRefillTimestamp(now);
            hashCacheService.put(RATE_LIMITING_HASH_KEY, ipAddress, bucket, cacheTtl, ipAddress);
            return false;
        }
    }
}
