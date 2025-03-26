package com.plusproject.domain.lock.lettuce.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class LettuceRepository {

    private static final long LOCK_TIMEOUT_SECONDS = 5;                 // TTL, 5초
    private static final long RETRY_TIMEOUT_MILLIS = 10_000;            // 락 획득 실패시 대기 시간 10초
    private static final long RETRY_DELAY_MILLIS = 100;                 // 락 획득 실패시 획득 재시도 시간 0.1초

    private final StringRedisTemplate redisTemplate;
    
    // Lettuce 로 순서 보장 로직은 포기했음
    public String acquireLock(String key) throws InterruptedException {
        String value = UUID.randomUUID().toString();
        long deadline = System.currentTimeMillis() + RETRY_TIMEOUT_MILLIS;

        while (System.currentTimeMillis() < deadline) {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(      // 해당 key 가 존재하지 않으면 true, 존재하면 false
                key,
                value,
                Duration.ofSeconds(LOCK_TIMEOUT_SECONDS)
            );

            if (Boolean.TRUE.equals(success)) {
                return value;                   // 락 획득 성공
            }
            
            Thread.sleep(RETRY_DELAY_MILLIS);   // 100ms 대기 후 재시도
        }

        return null;        // 10초 대기 동안 락 획득 실패시 null 반환
    }

    public void releaseLock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (value != null && value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }

}
