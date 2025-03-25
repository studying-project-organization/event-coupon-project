package com.plusproject.domain.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.JwtUtil;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.UserCoupon.repository.UserCouponRepository;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.dto.request.ChangeUserRoleRequest;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserAdminControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private HttpHeaders headers;
    private User adminUser;
    private User targetUser;

    @Nested
    class 관리자_권한_발급_테스트 {
        @BeforeEach
        public void setUp() {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 데이터 초기화
            userCouponRepository.deleteAll();
            userRepository.deleteAll();
            couponRepository.deleteAll();


            // 관리자 사용자 생성
            String adminPassword = passwordEncoder.encode("!AdminPassword1");
            adminUser = User.toEntity("admin@example.com", adminPassword, "admin", "adminAddress", UserRole.ADMIN);
            userRepository.save(adminUser);

            // 대상 사용자 생성 (권한 변경 대상)
            String userPassword = passwordEncoder.encode("!UserPassword1");
            targetUser = User.toEntity("user@example.com", userPassword, "user", "userAddress", UserRole.USER);
            userRepository.save(targetUser);

            // JWT 토큰 생성 및 헤더 설정 (관리자 권한)
            String token = jwtUtil.createToken(adminUser.getId(), adminUser.getUserRole());
            headers.set("Authorization", token);
        }

        @Test
        public void 관리자_권한_발급_성공() {
            // Given
            ChangeUserRoleRequest request = new ChangeUserRoleRequest("ADMIN");
            HttpEntity<ChangeUserRoleRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/users/" + targetUser.getId() + "/admin",
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            // Then
            System.out.println("Response: " + response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Response: " + response.getBody());
            assertTrue(response.getBody().contains("유저 권한 변경에 성공했습니다."));

            // DB에서 권한 변경 확인
            User updatedUser = userRepository.findById(targetUser.getId()).orElse(null);
            assertEquals(UserRole.ADMIN, updatedUser.getUserRole());
        }

        @Test
        public void 존재하지_않는_사용자에게_관리자_권한_발급_요청_시_실패() {
            // Given
            ChangeUserRoleRequest request = new ChangeUserRoleRequest("ADMIN");
            HttpEntity<ChangeUserRoleRequest> entity = new HttpEntity<>(request, headers);
            Long nonExistentUserId = 999L; // 존재하지 않는 ID

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/users/" + nonExistentUserId + "/admin",
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            // Then
            System.out.println("Response: " + response.getBody());
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Response: " + response.getBody());
            assertTrue(response.getBody().contains(ErrorCode.NOT_FOUND_USER.getMessage()));
        }

        @Test
        public void 관리자_권한이_없는_사용자가_관리자_권한_발급_요청_시_실패() {
            // Given: 일반 사용자 토큰으로 변경
            String normalUserPassword = passwordEncoder.encode("!NormalUserPassword1");
            User normalUser = User.toEntity("normal@example.com", normalUserPassword, "normal", "normalAddress", UserRole.USER);
            userRepository.save(normalUser);
            String normalUserToken = jwtUtil.createToken(normalUser.getId(), normalUser.getUserRole());
            headers.set("Authorization", normalUserToken);

            ChangeUserRoleRequest request = new ChangeUserRoleRequest("ADMIN");
            HttpEntity<ChangeUserRoleRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/users/" + targetUser.getId() + "/admin",
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            // Then
            System.out.println("Response: " + response.getBody());
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "Response: " + response.getBody());
            assertTrue(response.getBody().contains("관리자 권한이 필요합니다."));
        }

    }
}