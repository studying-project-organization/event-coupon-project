package com.plusproject.common.aop;

import com.plusproject.common.annotation.RedissonDistributedLock;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
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
    private final HttpServletRequest request;

    @Around("@annotation(com.plusproject.common.annotation.RedissonDistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String requestId = request.getRequestId();

        RedissonDistributedLock lockAnnotation = method.getAnnotation(RedissonDistributedLock.class);

        // 락 키 생성
        String lockKey = "lock:coupon:" + lockAnnotation.key();
        RLock lock = redissonClient.getFairLock(lockKey);

        log.info("Trying to acquire lock for key: {}", requestId);
        // 10초 내로 락 획득 시도
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                log.info("Lock acquired for key: {}", requestId);
                return joinPoint.proceed();
            } finally {
                lock.unlock(); // 작업 완료 후 락 해제
                log.info("Lock released for key: {}", requestId);
            }
        } else {
            log.warn("Failed to acquire lock for key: {}", lockKey);
            throw new ApplicationException(ErrorCode.ACQUISITION_FAILED_LOCK);
        }
    }
}
