package com.plusproject.common.auth;

import com.plusproject.common.annotation.Auth;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.enums.UserRole;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthAnnotation = parameter.getParameterAnnotation(Auth.class) != null;
        boolean isAuthUserType = parameter.getParameterType().equals(AuthUser.class);

        if (hasAuthAnnotation != isAuthUserType) {
            throw new ApplicationException(ErrorCode.INVALID_AUTH_ANNOTATION_USAGE);
        }

        return true;
    }

    @Override
    public Object resolveArgument(
        @Nullable MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        @Nullable NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Long userId = (Long) request.getAttribute("userId");
        UserRole userRole = UserRole.of((String) request.getAttribute("userRole"));

        return AuthUser.builder()
            .id(userId)
            .userRole(userRole)
            .build();
    }

}
