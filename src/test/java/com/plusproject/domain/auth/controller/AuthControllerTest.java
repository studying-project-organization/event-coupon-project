package com.plusproject.domain.auth.controller;

import com.plusproject.ControllerTestSupport;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.auth.dto.request.SigninRequest;
import com.plusproject.domain.auth.dto.request.SignupRequest;
import com.plusproject.domain.auth.dto.response.AccessTokenResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class AuthControllerTest extends ControllerTestSupport {

    private static final String PATH = "/api/v1/auth";

    @Test
    @DisplayName("회원가입 - 성공")
    void signup1() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .nickname("테스트")
            .address("서울")
            .build();

        // when & then
        mockMvc.perform(post(PATH + "/signup")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다."));

    }

    @Test
    @DisplayName("로그인 - 성공")
    void signin1() throws Exception {
        // given
        SigninRequest request = SigninRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .build();

        AccessTokenResponse response = AccessTokenResponse
            .builder()
            .accessToken("Bearer TokenData")
            .build();

        // when
        when(authService.signin(any(SigninRequest.class)))
            .thenReturn(response);

        // then
        mockMvc.perform(post(PATH + "/signin")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
            .andExpect(jsonPath("$.data.accessToken").value(containsString("Bearer ")));
    }

    @Test
    @DisplayName("로그인 - NOT_FOUND_USER 예외 발생")
    void signin2() throws Exception {
        // given
        SigninRequest request = SigninRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(authService).signin(any(SigninRequest.class));

        // then
        mockMvc.perform(post(PATH + "/signin")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage()));
    }

    @Test
    @DisplayName("로그인 - INCORRECT_PASSWORD 예외 발생")
    void signin3() throws Exception {
        // given
        SigninRequest request = SigninRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.INCORRECT_PASSWORD))
            .when(authService).signin(any(SigninRequest.class));

        // then
        mockMvc.perform(post(PATH + "/signin")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
            )
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.INCORRECT_PASSWORD.getMessage()));
    }

}