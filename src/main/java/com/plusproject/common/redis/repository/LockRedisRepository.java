package com.plusproject.common.redis.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class LockRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean acquireLock(String key, String value, long timeout) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
        return success != null && success;
    }

    public void releaseLock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (currentValue != null && currentValue.equals(value)) {
            redisTemplate.delete(key);
        }
    }
}