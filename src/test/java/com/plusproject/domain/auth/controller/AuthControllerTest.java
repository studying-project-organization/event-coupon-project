package com.plusproject.domain.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.UserCoupon.repository.UserCouponRepository;
import com.plusproject.domain.auth.dto.request.SigninRequest;
import com.plusproject.domain.auth.dto.request.SignupRequest;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserCouponRepository userCouponRepository;

    @Nested
    class 회원가입_로그인_테스트 {
        @BeforeEach
        public void setUp() {
            userCouponRepository.deleteAll();
            userRepository.deleteAll();
        }

        @Test
        void 회원가입_시도_성공() throws Exception {
            // Given
            SignupRequest request = new SignupRequest("test1@example.com", "!Password1", "nickname1", "address");

            // When & Then
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("회원 가입에 성공했습니다.")))
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("bearerToken")));

            // DB에 저장된 사용자 확인
            User savedUser = userRepository.findByEmail("test1@example.com").orElse(null);
            assertNotNull(savedUser);
            assertEquals("nickname1", savedUser.getNickname());
            assertTrue(passwordEncoder.matches("!Password1", savedUser.getPassword()));
        }

        @Test
        void 중복된_이메일로_회원가입_시도_시_실패() throws Exception {
            // Given: 이미 존재하는 사용자
            User existingUser = User.toEntity("test1@example.com", passwordEncoder.encode("!Password1"), "nickname1",
                    "address", UserRole.USER);
            userRepository.save(existingUser);

            SignupRequest request = new SignupRequest("test1@example.com", "!Password1234", "nickname2", "address2");

            // When & Then
            mockMvc.perform(post("/api/v1/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(ErrorCode.BAD_REQUEST_EXIST_EMAIL.getMessage())));
        }

        @Test
        void 로그인_시도_성공() throws Exception {
            // Given: 가입된 사용자
            User user = User.toEntity("test1@example.com", passwordEncoder.encode("!Password1"), "nickname1", "address",
                    UserRole.USER);
            userRepository.save(user);

            SigninRequest request = new SigninRequest("test1@example.com", "!Password1");

            // When & Then
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("로그인에 성공했습니다.")))
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("bearerToken")));
        }

        @Test
        void 틀린_비밀번호로_로그인_시도_시_실패() throws Exception {
            // Given: 가입된 사용자
            User user = User.toEntity("test1@example.com", passwordEncoder.encode("!Password1"), "nickname1", "address",
                    UserRole.USER);
            userRepository.save(user);

            SigninRequest request = new SigninRequest("test1@example.com", "!Password1234");

            // When & Then
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(ErrorCode.UNAUTHORIZED_WRONG_PASSWORD.getMessage())));
        }

        @Test
        void 존재하지_않는_사용자로_로그인_시도_시_실패() throws Exception {
            // Given: 존재하지 않는 사용자
            SigninRequest request = new SigninRequest("test1@example.com", "!Password1");

            // When & Then
            mockMvc.perform(post("/api/v1/auth/signin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(ErrorCode.BAD_REQUEST_NOT_SIGN_UP_USER.getMessage())));
        }
    }
}