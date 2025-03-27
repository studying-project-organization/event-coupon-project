package com.plusproject.domain.redisLettuce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisLettuceService {

    /*private final RedisLettuceRepository redisLettuceRepository;

    private static final String LOCK_PREFIX = "lock:coupon:";

    // Lock 획득시 실행
    public void executeWithLock(Long key, Runnable task) throws InterruptedException {
        String newKey = LOCK_PREFIX + key;
        String value = redisLettuceRepository.acquireLock(newKey) ;

        if (value == null) {
            throw new ApplicationException(ErrorCode.ACQUISITION_FAILED_LOCK);
        }

        try {
            task.run();
        } finally {
            redisLettuceRepository.releaseLock(newKey, value);
        }
    }*/
}
