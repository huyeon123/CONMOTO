package com.huyeon.superspace.global.support;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class CacheUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    public Map<String, Object> findCache(String key) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        return ops.entries(key);
    }

    public Object findCache(String key, String hashKey) {
        HashOperations<String, String, Object> ops = redisTemplate.opsForHash();
        return ops.get(key, hashKey);
    }

    public void saveCache(String key, Map<String, Object> value) {
        redisTemplate.opsForHash().putAll(key, value);
    }

    public void setExpire(String key, long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }
}
