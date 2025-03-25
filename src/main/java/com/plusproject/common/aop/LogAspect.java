package com.plusproject.common.aop;

import com.plusproject.common.log.service.LogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {
    private final LogService logService;

    @AfterReturning(pointcut = "execution(* com.plusproject.domain.coupon.controller.CouponController.createCoupon(..))", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String message = String.format("메서드 '%s'가 호출되었습니다. 반환 값: %s", methodName, result);
        logService.log("INFO", message);
    }

    @AfterThrowing(pointcut = "execution(* com.plusproject.domain.coupon.controller.CouponController.createCoupon(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String message = String.format("메서드 '%s'에서 오류 발생: %s", methodName, exception.getMessage());
        logService.log("ERROR", message);
    }
}
