package com.plusproject.common.aop;

import com.plusproject.common.annotation.DistributedLock;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.redis.lettuce.repository.RedisLettuceRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private final RedisLettuceRepository redisLettuceRepository;
    private final HttpServletRequest request;

    @Around("@annotation(com.plusproject.common.annotation.DistributedLock)")
    public Object lock(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String requestId = request.getRequestId();

        DistributedLock lockAnnotation = method.getAnnotation(DistributedLock.class);

        // 락 키 생성
        String lockKey = "lock:coupon:" + lockAnnotation.key();
        long waitTime = lockAnnotation.waitTime();
        long lockTime = lockAnnotation.lockTime();

        log.info("Trying to acquire lock for key: {}", requestId);
        // 락 획득 시도
        String lockValue = redisLettuceRepository.acquireLock(lockKey, waitTime, lockTime / 1000);

        if (lockValue == null) {
            log.warn("Failed to acquire lock for key: {}", lockKey);
            throw new ApplicationException(ErrorCode.ACQUISITION_FAILED_LOCK);
        }

        try {
            // 비즈니스 로직 실행
            log.info("Lock acquired for key: {}", requestId);
            return joinPoint.proceed();
        } finally {
            // 락 해제
            redisLettuceRepository.releaseLock(lockKey, lockValue);
            log.info("Lock released for key: {}", requestId);
        }
    }
}