package com.plusproject.redis;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.plusproject.common.exception.ErrorCode.REQUEST_TIMEOUT;

@Service
@RequiredArgsConstructor
public class LettuceRepository {

    private final StringRedisTemplate redisTemp;
    private static final long RETRY_DELAY_MILLIS = 100;

    /**
     * Redis Lock 획득 메서드
     * -> 현재 트랜잭션에 해당하는 작업을 Lock
     */
    public boolean acquireLock(String key, String value, long expireTime) throws InterruptedException {
        long deadLine = System.currentTimeMillis() + expireTime;
        Boolean isLockSuccess = false;

        while (System.currentTimeMillis() < deadLine){
            isLockSuccess = redisTemp.opsForValue().setIfAbsent(key, value, Duration.ofSeconds(expireTime));

            if (Boolean.FALSE.equals(isLockSuccess)) {
                Thread.sleep(RETRY_DELAY_MILLIS);
            }
        }

        if (Boolean.FALSE.equals(isLockSuccess)) {
            throw new ApplicationException(REQUEST_TIMEOUT);
        }

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
