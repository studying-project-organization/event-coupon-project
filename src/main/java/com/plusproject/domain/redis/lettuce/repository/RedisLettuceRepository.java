package com.plusproject.domain.redis.lettuce.repository;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisLettuceRepository {

    private final RedisTemplate<String, String> redisTemplate;

    // 락 획득 시도
    public String acquireLock(String key, long waitTimeMillis, long lockTimeSeconds) throws InterruptedException {
        String value = UUID.randomUUID().toString();
        long deadline = System.currentTimeMillis() + waitTimeMillis;

        while (System.currentTimeMillis() < deadline) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(
                    key,
                    value,
                    Duration.ofSeconds(lockTimeSeconds)
            );

            if (Boolean.TRUE.equals(success)) {
                return value; // 락 획득 성공
            }

            Thread.sleep(100); // 100ms 대기 후 재시도
        }

        return null; // 락 획득 실패
    }

    // 락 해제
    public void releaseLock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (value != null && value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }
}
