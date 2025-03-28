package com.plusproject.common.auth;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private static final Map<String, String[]> WHITE_LIST = Map.of(
        "POST", new String[]{
            "/api/v1/auth/signup",
            "/api/v1/auth/signin"
        }
    );

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (isWhiteList(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String bearer = httpRequest.getHeader("Authorization");

        try {
            if (bearer == null || bearer.isEmpty()) {
                throw new ApplicationException(ErrorCode.REQUIRED_JWT_TOKEN);
            }

            String jwt = jwtUtil.substringToken(bearer);

            try {
                Claims claims = jwtUtil.extractClaims(jwt);
                if (claims.isEmpty()) {
                    throw new ApplicationException(ErrorCode.INVALID_JWT_TOKEN);
                }

                httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
                httpRequest.setAttribute("userRole", claims.get("userRole"));

                chain.doFilter(request, response);

            } catch (SecurityException | MalformedJwtException ex) {
                throw new ApplicationException(ErrorCode.INVALID_JWT_SIGNATURE);
            } catch (ExpiredJwtException ex) {
                throw new ApplicationException(ErrorCode.EXPIRED_JWT_TOKEN);
            } catch (UnsupportedJwtException ex) {
                throw new ApplicationException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
            } catch (MissingRequestHeaderException | AccessDeniedException ex) {
                throw new ApplicationException(ErrorCode.MISSING_JWT_TOKEN);
            }
        } catch (ApplicationException ex) {
            parseResponseErrorMessage(httpResponse, ex);
        }
    }

    private void parseResponseErrorMessage(HttpServletResponse httpResponse, ApplicationException ex) throws IOException {
        httpResponse.setStatus(ex.getStatus().value());
        httpResponse.setContentType("application/json;charset=UTF-8");

        String errorBody = String.format("""
            {
                "status": "%s",
                "code": "%d",
                "message": "%s",
                "timestamp": "%s"
            }
            """, ex.getStatus().name(), ex.getStatus().value(), ex.getMessage(), LocalDateTime.now());

        PrintWriter writer = httpResponse.getWriter();
        writer.println(errorBody);
        writer.flush();
    }

    private boolean isWhiteList(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();

        if (!WHITE_LIST.containsKey(method)) {
            return false;
        }

        String[] lists = WHITE_LIST.get(method);
        return PatternMatchUtils.simpleMatch(lists, path);
    }
}
