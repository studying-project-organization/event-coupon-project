package com.plusproject.redis.redisson;

import com.plusproject.redis.DistributedLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedissonLockImpl implements DistributedLockManager {

    private static final String LOCK_KEY_PREFIX = "redisson-lock:";

    private final RedissonClient redissonClient;

    @Override
    public void executeWithLock(Long key, Runnable task) throws InterruptedException {
        String lockKey = LOCK_KEY_PREFIX + key;
        RLock lock = redissonClient.getFairLock(lockKey);       // 공정 락

        for (int i = 0; i < 3; i++) {
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    task.run();
                    return;
                } finally {
                    lock.unlock();
                }
            }
            Thread.sleep(200);
        }

        throw new ApplicationException(ErrorCode.ACQUISITION_FAILED_LOCK, " = " + lockKey);
    }
}
