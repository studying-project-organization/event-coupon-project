package com.plusproject.redis.lettuce;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.redis.DistributedLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LettuceService implements DistributedLockManager {

    private final LettuceRepository lettuceRepository;

    @Override
    public void executeWithLock(Long key, Runnable task) throws InterruptedException {
        String newKey = "lock:coupon:" + key;
        String value = lettuceRepository.acquireLock(newKey) ;

        if (value == null) {
            throw new ApplicationException(ErrorCode.ACQUISITION_FAILED_LOCK);
        }

        try {
            task.run();
        } finally {
            lettuceRepository.releaseLock(newKey, value);
        }
    }
}
