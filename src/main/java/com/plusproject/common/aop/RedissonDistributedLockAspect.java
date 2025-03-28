package com.plusproject.common.aop;

import com.plusproject.common.annotation.RedissonDistributedLock;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RedissonDistributedLockAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(com.plusproject.common.annotation.RedissonDistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        RedissonDistributedLock lockAnnotation = method.getAnnotation(RedissonDistributedLock.class);

        // 락 키 생성
        String lockKey = "lock:coupon:" + lockAnnotation.key();
        RLock lock = redissonClient.getFairLock(lockKey);

        // 20초 내로 락 획득 시도
        if (lock.tryLock(20, TimeUnit.SECONDS)) {
            try {
                return joinPoint.proceed();
            } finally {
                lock.unlock(); // 작업 완료 후 락 해제
            }
        } else {
            throw new ApplicationException(ErrorCode.ACQUISITION_FAILED_LOCK);
        }
    }
}
