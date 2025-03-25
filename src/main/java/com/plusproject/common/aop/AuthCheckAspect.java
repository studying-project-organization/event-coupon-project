package com.plusproject.common.aop;


import com.plusproject.common.annotation.AuthCheck;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect // 이 클래스가 AOP(Aspect Oriented Programming) 기능을 수행함을 나타냄
@Slf4j // Lombok을 사용한 로깅 기능을 추가
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
public class AuthCheckAspect {

    // AuthCheck 어노테이션이 붙은 메서드를 타겟으로 하는 포인트컷 정의
    @Pointcut("@annotation(com.plusproject.common.annotation.AuthCheck)")
    private void AuthCheckPoint() {
        // 포인트컷의 본체는 비어있음, 메서드 이름으로 사용됨
    }

    // AuthCheck 포인트컷과 함께 AuthCheck 어노테이션을 가진 메서드에 대해 실행되는 어드바이스
    @Around("AuthCheckPoint() && @annotation(authCheck)")
    public Object checkUserRole(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 현재 HTTP 요청을 가져옴
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // 요청에서 userRole 속성을 가져옴
        Object userRoleObj = request.getAttribute("userRole");
        // userRole이 String 타입인지 확인 후, 안전하게 변환
        String userRole = (userRoleObj instanceof String) ? (String) userRoleObj : null;

        // userRole이 null일 경우 경고 로그를 남기고 예외 발생
        if (userRole == null) {
            log.warn("User role is not set in the request attributes.");
            throw new ApplicationException(ErrorCode.UNAUTHORIZED, "User role is null.");
        }

        // 현재 사용자의 역할을 로깅
        log.info("role = {}", userRole);

        // 요청된 userRole이 어노테이션에 정의된 역할과 일치하지 않을 경우 예외 발생
        if (!userRole.equals(authCheck.value().name())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }

        // 역할 체크가 완료되면 원래의 메서드를 호출
        return joinPoint.proceed();
    }
}
