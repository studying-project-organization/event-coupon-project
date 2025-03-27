package com.plusproject.domain.redisLettuce.repository;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class RedisLettuceRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long LOCK_TIMEOUT_SECONDS = 5;
    private static final long RETRY_TIMEOUT_MILLIS = 10_000;
    private static final long RETRY_DELAY_MILLIS = 100;

    // 락 획득 시도
    public String acquireLock(String key) throws InterruptedException {
        String value = UUID.randomUUID().toString();
        long deadline = System.currentTimeMillis() + RETRY_TIMEOUT_MILLIS;

        while (System.currentTimeMillis() < deadline) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(
                    key,
                    value,
                    Duration.ofSeconds(LOCK_TIMEOUT_SECONDS)
            );

            if (Boolean.TRUE.equals(success)) {
                return value;                   // 락 획득 성공
            }

            Thread.sleep(RETRY_DELAY_MILLIS);   // 100ms 대기 후 재시도
        }

        return null;
    }

    // 락 해제
    public void releaseLock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (value != null && value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }
}
