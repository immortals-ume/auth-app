package com.immortals.authapp.service;

import com.immortals.authapp.service.cache.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisTokenBlacklistService implements TokenBlacklistService {

    private final CacheService<String, Object> cacheService;

    private static final String BLACKLIST_PREFIX = "blacklisted_token:";

    @Override
    public void blacklistToken(String token, long ttlInMillis) {
        String key = BLACKLIST_PREFIX + token;
        cacheService.put(key, "blacklisted", Duration.ofSeconds(ttlInMillis / 1000), UUID.randomUUID().toString());
    }

    public boolean isTokenBlacklisted(String token) {
        return cacheService.containsKey(BLACKLIST_PREFIX + token, UUID.randomUUID().toString());
    }
}
