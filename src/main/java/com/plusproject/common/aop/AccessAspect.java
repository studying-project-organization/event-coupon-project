package com.plusproject.common.aop;

import com.plusproject.common.annotation.Access;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AccessAspect {

    private final HttpServletRequest request;

    @Around("@annotation(access)")
    public Object apiAccess(ProceedingJoinPoint joinPoint, Access access) throws Throwable {
        UserRole value = access.value();
        UserRole userRole = UserRole.of((String) request.getAttribute("userRole"));

        if (value.equals(UserRole.ADMIN)) {
            String userId = String.valueOf(request.getAttribute("userId"));
            String requestURI = request.getRequestURI();
            LocalDateTime requestTime = LocalDateTime.now();

            log.info("Admin Access Log - User ID: {}, Request Time: {}, Request URL: {}, Method: {}",
                userId, requestTime, requestURI, joinPoint.getSignature().getName());

            if (!value.equals(userRole)) {
                throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY);
            }

        } else {
            if (!value.equals(userRole)) {
                throw new ApplicationException(ErrorCode.FORBIDDEN_USER_ONLY);
            }
        }

        return joinPoint.proceed();
    }

}