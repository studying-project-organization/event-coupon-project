package com.plusproject.domain.user.controller;

import com.plusproject.ControllerTestSupport;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.dto.request.UpdateAdminRequest;
import com.plusproject.domain.user.dto.request.UpdatePasswordRequest;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    private static final String PATH = "/api/v1/users";

    private String accessToken;

    @BeforeEach
    void setUp() {
        accessToken = jwtUtil.createAccessToken(1L, UserRole.ADMIN);
    }

    @Test
    @DisplayName("유저 권한 변경 - 성공")
    void updateUserRole1() throws Exception {
        // given
        UpdateAdminRequest request = UpdateAdminRequest.builder()
            .userRole(UserRole.ADMIN)
            .build();

        // when & then
        mockMvc.perform(put(PATH + "/{userId}/admin", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("유저 권한 변경에 성공했습니다."));
    }

    @Test
    @DisplayName("유저 권한 변경 - INVALID_SELF_ROLE_UPDATE 예외 발생")
    void updateUserRole2() throws Exception {
        // given
        UpdateAdminRequest request = UpdateAdminRequest.builder()
            .userRole(UserRole.ADMIN)
            .build();

        doThrow(new ApplicationException(ErrorCode.INVALID_SELF_ROLE_UPDATE))
            .when(userService)
            .updateUserRole(any(AuthUser.class), anyLong(), any(UpdateAdminRequest.class));

        // when & then
        mockMvc.perform(put(PATH + "/{userId}/admin", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_SELF_ROLE_UPDATE.getMessage()));
    }

    @Test
    @DisplayName("유저 권한 변경 - NOT_FOUND_USER 예외 발생")
    void updateUserRole3() throws Exception {
        // given
        UpdateAdminRequest request = UpdateAdminRequest.builder()
            .userRole(UserRole.ADMIN)
            .build();

        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(userService)
            .updateUserRole(any(AuthUser.class), anyLong(), any(UpdateAdminRequest.class));

        // when & then
        mockMvc.perform(put(PATH + "/{userId}/admin", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage()));
    }

    @Test
    @DisplayName("유저 권한 변경 - INVALID_ADMIN_ROLE_UPDATE 예외 발생")
    void updateUserRole4() throws Exception {
        // given
        UpdateAdminRequest request = UpdateAdminRequest.builder()
            .userRole(UserRole.ADMIN)
            .build();

        doThrow(new ApplicationException(ErrorCode.INVALID_ADMIN_ROLE_UPDATE))
            .when(userService)
            .updateUserRole(any(AuthUser.class), anyLong(), any(UpdateAdminRequest.class));

        // when & then
        mockMvc.perform(put(PATH + "/{userId}/admin", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_ADMIN_ROLE_UPDATE.getMessage()));
    }

    @Test
    @DisplayName("유저 비밀번호 변경 - 성공")
    void updatePassword1() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when & then
        mockMvc.perform(put(PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("비밀번호 변경에 성공했습니다."));
    }

    @Test
    @DisplayName("유저 비밀번호 변경 - NOT_FOUND_USER 예외 발생")
    void updatePassword2() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.NOT_FOUND_USER))
            .when(userService)
            .updatePassword(any(AuthUser.class), any(UpdatePasswordRequest.class));

        // then
        mockMvc.perform(put(PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value(ErrorCode.NOT_FOUND_USER.getMessage()));
    }

    @Test
    @DisplayName("유저 비밀번호 변경 - SAME_AS_OLD_PASSWORD 예외 발생")
    void updatePassword3() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.SAME_AS_OLD_PASSWORD))
            .when(userService)
            .updatePassword(any(AuthUser.class), any(UpdatePasswordRequest.class));

        // then
        mockMvc.perform(put(PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(ErrorCode.SAME_AS_OLD_PASSWORD.getMessage()));
    }

    @Test
    @DisplayName("유저 비밀번호 변경 - INCORRECT_PASSWORD 예외 발생")
    void updatePassword4() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when
        doThrow(new ApplicationException(ErrorCode.INCORRECT_PASSWORD))
            .when(userService)
            .updatePassword(any(AuthUser.class), any(UpdatePasswordRequest.class));

        // then
        mockMvc.perform(put(PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value(ErrorCode.INCORRECT_PASSWORD.getMessage()));
    }

}