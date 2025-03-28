package com.plusproject.common.redis.service;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.common.redis.repository.LockRedisRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class LockService {

    private final LockRedisRepository lockRedisRepository;

    public String lock(String key, long timeout) {
        String value = UUID.randomUUID().toString();
        int retryCount = 0;

        while (retryCount < 5) { // 최대 5번 재시도
            boolean acquired = lockRedisRepository.acquireLock(key, value, timeout);
            if (acquired) {
                return value; // Lock을 획득한 값을 반환
            }
            retryCount++;
            try {
                Thread.sleep(100); // 100ms 대기 후 재시도
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ApplicationException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }
        }
        throw new ApplicationException(ErrorCode.LOCK_ACQUISITION_FAILED); // Lock 획득 실패 처리
    }

    public void unlock(String key, String value) {
        lockRedisRepository.releaseLock(key, value);
    }
}
