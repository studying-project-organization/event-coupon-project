package com.plusproject.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final UserService userServ;

    private static final List<String> EXCLUDED_URIS = List.of(
            "/users/signup",
            "/users/login"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        if (isExcludedUri(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new ApplicationException(ErrorCode.TOKEN_MISSING, "토큰이 존재하지 않습니다.");
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                throw new ApplicationException(ErrorCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);

            userServ.findUser(userId);

            request.setAttribute("userId", userId);

            chain.doFilter(request, response);

        } catch (ApplicationException e) {
            handleException(e, (HttpServletResponse) response);
        }
    }

    private void handleException(ApplicationException e, HttpServletResponse response) throws IOException {
        response.setStatus(e.getStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorResponse = Map.of(
                "type", e.getExceptionType(),
                "message", e.getMessage()
        );

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private boolean isExcludedUri(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        return EXCLUDED_URIS.contains(requestUri);
    }
}
