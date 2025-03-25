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
import com.plusproject.domain.user.dto.request.ChangeUserRoleRequest;
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
class UserAdminControllerTest {

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

    private User adminUser;
    private User targetUser;

    @Nested
    class 관리자_권한_발급_테스트 {
        @BeforeEach
        public void setUp() {
            // 데이터 초기화
            userCouponRepository.deleteAll();
            userRepository.deleteAll();

            // 관리자 사용자 생성
            String adminPassword = passwordEncoder.encode("!AdminPassword1");
            adminUser = User.toEntity("admin@example.com", adminPassword, "admin", "adminAddress", UserRole.ADMIN);
            userRepository.save(adminUser);

            // 대상 사용자 생성 (권한 변경 대상)
            String userPassword = passwordEncoder.encode("!UserPassword1");
            targetUser = User.toEntity("user@example.com", userPassword, "user", "userAddress", UserRole.USER);
            userRepository.save(targetUser);
        }

        @Test
        public void 관리자_권한_발급_성공() throws Exception {
            // Given
            ChangeUserRoleRequest request = new ChangeUserRoleRequest("ADMIN");
            String token = jwtUtil.createToken(adminUser.getId(), adminUser.getUserRole());

            // When & Then
            mockMvc.perform(put("/api/v1/users/" + targetUser.getId() + "/admin")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("유저 권한 변경에 성공했습니다.")));

            // DB에서 권한 변경 확인
            User updatedUser = userRepository.findById(targetUser.getId()).orElse(null);
            assertEquals(UserRole.ADMIN, updatedUser.getUserRole());
        }

        @Test
        public void 존재하지_않는_사용자에게_관리자_권한_발급_요청_시_실패() throws Exception {
            // Given
            ChangeUserRoleRequest request = new ChangeUserRoleRequest("ADMIN");
            String token = jwtUtil.createToken(adminUser.getId(), adminUser.getUserRole());
            Long nonExistentUserId = 999L; // 존재하지 않는 ID

            // When & Then
            mockMvc.perform(put("/api/v1/users/" + nonExistentUserId + "/admin")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(ErrorCode.NOT_FOUND_USER.getMessage())));
        }

        @Test
        public void 관리자_권한이_없는_사용자가_관리자_권한_발급_요청_시_실패() throws Exception {
            // Given: 일반 사용자 토큰으로 변경
            String normalUserPassword = passwordEncoder.encode("!NormalUserPassword1");
            User normalUser = User.toEntity("normal@example.com", normalUserPassword, "normal", "normalAddress", UserRole.USER);
            userRepository.save(normalUser);
            String normalUserToken = jwtUtil.createToken(normalUser.getId(), normalUser.getUserRole());

            ChangeUserRoleRequest request = new ChangeUserRoleRequest("ADMIN");

            // When & Then
            mockMvc.perform(put("/api/v1/users/" + targetUser.getId() + "/admin")
                            .header("Authorization", normalUserToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("관리자 권한이 필요합니다.")));
        }
    }
}