package com.plusproject.domain.user.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.JwtUtil;
import com.plusproject.config.LocalDateTimeConverter;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.UserCoupon.Entity.UserCoupon;
import com.plusproject.domain.UserCoupon.enums.CouponStatus;
import com.plusproject.domain.UserCoupon.repository.UserCouponRepository;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.dto.request.UserPasswordUpdateRequest;
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
class UserControllerTest {

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

    @Nested
    class 비밀번호_테스트{
        private HttpHeaders headers;
        private User testUser;
        private Coupon testCoupon;

        @BeforeEach
        public void setUp(){
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            userCouponRepository.deleteAll();
            userRepository.deleteAll();
            couponRepository.deleteAll();

            // 테스트용 사용자 생성
            String encodedPassword = passwordEncoder.encode("!Password1");
            testUser = User.toEntity("test@example.com", encodedPassword, "nickname", "address", UserRole.USER);
            userRepository.save(testUser);

            // JWT 토큰 생성 및 헤더 설정
            String token = jwtUtil.createToken(testUser.getId(), testUser.getUserRole());
            headers.set("Authorization", token);
        }

        @Test
        public void 비밀번호_변경_성공() {
            // Given
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("!Password1", "!NewPassword1");
            HttpEntity<UserPasswordUpdateRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("비밀번호 변경에 성공했습니다."));

            // DB에서 변경된 비밀번호 확인
            User updatedUser = userRepository.findById(testUser.getId()).orElse(null);
            assertTrue(passwordEncoder.matches("!NewPassword1", updatedUser.getPassword()));
        }

        @Test
        public void 잘못된_기존_비밀번호로_변경_시도_시_실패() {
            // Given
            UserPasswordUpdateRequest request = new UserPasswordUpdateRequest("!WrongPassword", "!NewPassword1");
            HttpEntity<UserPasswordUpdateRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/users",
                    HttpMethod.PUT,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().contains(ErrorCode.BAD_REQUEST_NOT_MATCH_OLD_PASSWORD.getMessage()));
        }
    }

    @Nested
    class 쿠폰_발급_관련_테스트{
        private HttpHeaders headers;
        private User testUser;
        private Coupon testCoupon;

        @BeforeEach
        public void setUp(){
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 데이터 초기화
            userCouponRepository.deleteAll();
            userRepository.deleteAll();
            couponRepository.deleteAll();

            // 테스트용 사용자 생성
            String encodedPassword = passwordEncoder.encode("!Password1");
            testUser = User.toEntity("test@example.com", encodedPassword, "nickname", "address", UserRole.USER);
            userRepository.save(testUser);

            // JWT 토큰 생성 및 헤더 설정
            String token = jwtUtil.createToken(testUser.getId(), testUser.getUserRole());
            headers.set("Authorization", token);

            // 테스트용 쿠폰 생성
            testCoupon = Coupon.toEntity( "Test Coupon", "Test Description", 1000, 10,
                    localDateTimeConverter.toLocalDateTime("2025-03-24 00:00:00"),
                    localDateTimeConverter.toLocalDateTime("2025-03-28 23:59:59"));
            couponRepository.save(testCoupon);
        }

        @Test
        public void 쿠폰_발급_성공() {
            // Given
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/users/coupons/" + testCoupon.getId(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().contains("쿠폰 발급에 성공했습니다."));

            // DB에서 쿠폰 발급 확인
            UserCoupon userCoupon = userCouponRepository.findByIdAndUserId(testCoupon.getId(), testUser.getId());
            assertEquals(CouponStatus.RECEIVE, userCoupon.getCouponStatus());
            assertEquals(9, couponRepository.findById(testCoupon.getId()).get().getQuantity()); // 수량 감소 확인
        }

        @Test
        public void 이미_발급된_쿠폰_재발급_시도_시_실패(){
            // Given: 이미 발급된 쿠폰
            UserCoupon userCoupon = UserCoupon.toEntity(testUser, testCoupon, CouponStatus.RECEIVE);
            userCouponRepository.save(userCoupon);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/users/coupons/" + testCoupon.getId(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().contains(ErrorCode.BAD_REQUEST_ALREADY_HAVE_COUPON.getMessage()));
        }

        @Test
        public void 쿠폰_조회_성공(){
            // Given: 발급된 쿠폰 추가
            UserCoupon userCoupon = UserCoupon.toEntity(testUser, testCoupon, CouponStatus.RECEIVE);
            userCouponRepository.save(userCoupon);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/users/coupons",
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            String responseBody = response.getBody();
            assertTrue(responseBody.contains("쿠폰 조회에 성공했습니다."));
            assertTrue(responseBody.contains("Test Coupon"));
            assertTrue(responseBody.contains("1000")); // discountAmount
        }
    }
}