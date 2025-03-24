package com.plusproject.common.aop;

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

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserAccessAspect {

    private final HttpServletRequest request;

    @Around("@annotation(com.plusproject.common.annotation.User)")
    public Object userApiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        UserRole userRole = (UserRole) request.getAttribute("userRole");

        if (!UserRole.USER.equals(userRole)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN_USER_ROLE_USER);
        }
        return joinPoint.proceed();
    }
}
