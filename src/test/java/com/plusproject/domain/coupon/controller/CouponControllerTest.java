package com.plusproject.domain.coupon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.plusproject.config.JwtUtil;
import com.plusproject.config.LocalDateTimeConverter;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.UserCoupon.repository.UserCouponRepository;
import com.plusproject.domain.coupon.dto.request.CouponCreateRequest;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
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
class CouponControllerTest {

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

    @Autowired
    private LocalDateTimeConverter localDateTimeConverter;

    private HttpHeaders headers;
    private User adminUser;

    @Nested
    class 쿠폰_생성_테스트 {
        @BeforeEach
        public void setUp() {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 데이터 초기화
            userCouponRepository.deleteAll();
            userRepository.deleteAll();
            couponRepository.deleteAll();

            // 관리자 사용자 생성
            String encodedPassword = passwordEncoder.encode("!AdminPassword1");
            adminUser = User.toEntity("admin@example.com", encodedPassword, "admin", "adminAddress", UserRole.ADMIN);
            userRepository.save(adminUser);

            // JWT 토큰 생성 및 헤더 설정
            String token = jwtUtil.createToken(adminUser.getId(), adminUser.getUserRole());
            headers.set("Authorization", token);
        }

        @Test
        public void 관리자가_쿠폰_생성_성공() {
            // Given
            CouponCreateRequest request = new CouponCreateRequest(
                    "New Coupon",
                    "Test Coupon Description",
                    2000,
                    5,
                    "2025-03-25 00:00:00",
                    "2025-03-30 23:59:59"
            );
            HttpEntity<CouponCreateRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/coupons",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            System.out.println("Response: " + response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode(), "Response: " + response.getBody());
            assertTrue(response.getBody().contains("쿠폰이 생성되었습니다."));

            // DB에서 쿠폰 생성 확인
            Coupon createdCoupon = couponRepository.findByName("New Coupon");
            assertTrue(createdCoupon != null);
            assertEquals("New Coupon", createdCoupon.getName());
            assertEquals(2000, createdCoupon.getDiscountAmount());
            assertEquals(5, createdCoupon.getQuantity());
            assertEquals(localDateTimeConverter.toLocalDateTime("2025-03-25 00:00:00"), createdCoupon.getStartDate());
            assertEquals(localDateTimeConverter.toLocalDateTime("2025-03-30 23:59:59"), createdCoupon.getEndDate());
        }

        @Test
        public void 유저가_쿠폰_생성_시도_시_실패() {
            // Given: 일반 사용자 토큰으로 변경
            String userEncodedPassword = passwordEncoder.encode("!UserPassword1");
            User normalUser = User.toEntity("user@example.com", userEncodedPassword, "user", "userAddress", UserRole.USER);
            userRepository.save(normalUser);
            String userToken = jwtUtil.createToken(normalUser.getId(), normalUser.getUserRole());
            headers.set("Authorization", userToken);

            CouponCreateRequest request = new CouponCreateRequest(
                    "Invalid Coupon",
                    "Test Coupon Description",
                    2000,
                    5,
                    "2025-03-25 00:00:00",
                    "2025-03-30 23:59:59"
            );
            HttpEntity<CouponCreateRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/coupons",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
            assertTrue(response.getBody().contains("관리자 권한이 필요합니다."));
        }

        @Test
        public void 데이터가_유효하지_않을_경우_쿠폰_생성_실패() {
            // Given: 유효하지 않은 데이터
            CouponCreateRequest request = new CouponCreateRequest(
                    "Invalid Coupon",
                    "Test Coupon Description",
                    null, //유효하지 않은 값.
                    5,
                    "2025-03-25 00:00:00",
                    "2025-03-30 23:59:59"
            );
            HttpEntity<CouponCreateRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/coupons",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().contains("쿠폰 할인 가격은 필수값입니다."));
        }
    }
}