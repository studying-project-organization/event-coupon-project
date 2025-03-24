package com.plusproject.common.aop;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminAccessAspect {

    private final HttpServletRequest request;

    @Around("@annotation(com.plusproject.common.annotation.Admin)")
    public Object adminApiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        String userId = String.valueOf(request.getAttribute("userId"));
        String requestUrl = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();
        UserRole userRole = (UserRole) request.getAttribute("userRole");

        log.info("Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
                userId, requestTime, requestUrl, joinPoint.getSignature().getName());

        if (!UserRole.ADMIN.equals(userRole)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_USER_ROLE_ADMIN);
        }
        return joinPoint.proceed();
    }
}
