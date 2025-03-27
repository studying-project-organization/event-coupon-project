package com.plusproject.common.lock;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedissonDistributedLockManager implements DistributedLockManager {

    private static final String LOCK_KEY_PREFIX = "redisson-lock:";

    private final RedissonClient redissonClient;

    @Override
    public void executeWithLock(Long key, Runnable task) throws InterruptedException {
        String lockKey = LOCK_KEY_PREFIX + key;
        RLock lock = redissonClient.getFairLock(lockKey);       // 공정 락

        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                task.run();
            } finally {
                lock.unlock();
            }
        } else {
            throw new ApplicationException(ErrorCode.ACQUISITION_FAILED_LOCK, " = " + lockKey);
        }
    }
}
