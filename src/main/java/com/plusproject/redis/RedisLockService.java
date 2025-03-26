package com.plusproject.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemp;

    /**
     * Redis Lock 획득 메서드
     * -> 현재 트랜잭션에 해당하는 작업을 Lock
     */
    public boolean acquireLock(String key, String value, Long expireTime) {
        Boolean isLockSuccess = redisTemp.opsForValue().setIfAbsent(key, value, Duration.ofMillis(expireTime));
        return Boolean.TRUE.equals(isLockSuccess);
    }

    /**
     * Redis Lock 해제 메서드
     * -> 현재 트랜잭션에 해당하는 작업을 중지함
     */
    public void releaseLock(String key, String value) {
        String currentTaskValue = redisTemp.opsForValue().get(key);
        if (value.equals(currentTaskValue)) {
            redisTemp.delete(key);
        }
    }
}
