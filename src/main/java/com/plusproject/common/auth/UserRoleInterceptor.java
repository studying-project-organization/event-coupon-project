package com.plusproject.common.auth;

import com.plusproject.common.annotation.AdminOnly;
import com.plusproject.common.annotation.UserOnly;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.enums.UserRole;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserRoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
      @Nullable HttpServletRequest request,
      @Nullable HttpServletResponse response,
      @Nullable Object handler
    )  {
        if (handler instanceof HandlerMethod handlerMethod) {
            AdminOnly adminOnly = handlerMethod.getMethodAnnotation(AdminOnly.class);
            UserOnly userOnly = handlerMethod.getMethodAnnotation(UserOnly.class);

            if (request != null && request.getAttribute("userRole") != null) {
                UserRole userRole = UserRole.of((String) request.getAttribute("userRole"));

                if (adminOnly != null && !userRole.equals(UserRole.ADMIN)) {
                    throw new ApplicationException(ErrorCode.FORBIDDEN_ADMIN_ONLY);
                }

                if (userOnly != null && !userRole.equals(UserRole.USER)) {
                    throw new ApplicationException(ErrorCode.FORBIDDEN_USER_ONLY);
                }
            }
        }

        return true;
    }
}
