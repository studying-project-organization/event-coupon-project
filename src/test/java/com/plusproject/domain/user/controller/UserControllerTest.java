package com.plusproject.domain.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.JwtUtil;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.UserCoupon.repository.UserCouponRepository;
import com.plusproject.domain.user.dto.request.UserPasswordUpdateRequest;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class 비밀번호_테스트 {
        private String token;
        private User testUser;

        @BeforeEach
        public void setUp() {
            userCouponRepository.deleteAll();
            userRepository.deleteAll();

            // 테스트용 사용자 생성
            String encodedPassword = passwordEncoder.encode("!Password1");
            testUser = User.toEntity("test@example.com", encodedPassword, "nickname", "address", UserRole.USER);
            userRepository.save(testUser);

            // JWT 토큰 생성
            token = jwtUtil.createToken(testUser.getId(), testUser.getUserRole());
        }

        @Test
        public void 비밀번호_변경_성공() throws Exception {
            // Given
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("!Password1", "!NewPassword1");

            // When & Then
            mockMvc.perform(put("/api/v1/users")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("비밀번호 변경에 성공했습니다.")));

            // DB에서 변경된 비밀번호 확인
            User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
            assertEquals(true, passwordEncoder.matches("!NewPassword1", updatedUser.getPassword()));
        }

        @Test
        public void 잘못된_기존_비밀번호로_변경_시도_시_실패() throws Exception {
            // Given
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("!WrongPassword", "!NewPassword1");

            // When & Then
            mockMvc.perform(put("/api/v1/users")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(ErrorCode.BAD_REQUEST_NOT_MATCH_OLD_PASSWORD.getMessage())));
        }

        @Test
        public void 새_비밀번호와_기존_비밀번호가_같을_경우_변경_시도_시_실패() throws Exception {
            // Given
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("!Password1", "!Password1");

            // When & Then
            mockMvc.perform(put("/api/v1/users")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(ErrorCode.BAD_REQUEST_DO_NOT_MATCH_OLD_AND_NEW_PASSWORD.getMessage())));
        }
    }
}