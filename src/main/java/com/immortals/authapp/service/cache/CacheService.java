package com.immortals.authapp.service.cache;

import java.time.Duration;
import java.util.Map;

public interface CacheService<K, V> {
    void put(K key, V value, Duration ttl,String lockingKey);

    Boolean putIfAbsent(K key, V value, Duration ttl,String lockingKey);

    void putMultipleIfAbsent(Map<K, V> entries, Duration ttl,String lockingKey);

    V get(K key,String lockingKey);

    void remove(K key,String lockingKey);

    void clear(String lockingKey);

    boolean containsKey(K key,String lockingKey);

    default Long getHitCount() {
        return 0L;
    }

    default Long getMissCount() {
        return 0L;
    }


}
