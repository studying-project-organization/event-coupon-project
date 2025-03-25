package com.plusproject.domain.coupon.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private User adminUser;

    @Nested
    class 쿠폰_생성_테스트 {

        @BeforeEach
        public void setUp() {
            // 데이터 초기화
            userCouponRepository.deleteAll();
            userRepository.deleteAll();
            couponRepository.deleteAll();

            // 관리자 사용자 생성
            String encodedPassword = passwordEncoder.encode("!AdminPassword1");
            adminUser = User.toEntity("admin@example.com", encodedPassword, "admin", "adminAddress", UserRole.ADMIN);
            userRepository.save(adminUser);

            // JWT 토큰 생성
            adminToken = jwtUtil.createToken(adminUser.getId(), adminUser.getUserRole());
        }

        @Test
        public void 관리자가_쿠폰_생성_성공() throws Exception {
            // Given
            CouponCreateRequest request = new CouponCreateRequest(
                    "New Coupon",
                    "Test Coupon Description",
                    2000,
                    5,
                    "2025-03-25 00:00:00",
                    "2025-03-30 23:59:59"
            );

            // When & Then
            mockMvc.perform(post("/api/v1/coupons")
                            .header("Authorization", adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("쿠폰이 생성되었습니다.")));

            // DB에서 쿠폰 생성 확인
            Coupon createdCoupon = couponRepository.findByName("New Coupon");
            assertNotNull(createdCoupon);
            assertEquals("New Coupon", createdCoupon.getName());
            assertEquals(2000, createdCoupon.getDiscountAmount());
            assertEquals(5, createdCoupon.getQuantity());
            assertEquals(localDateTimeConverter.toLocalDateTime("2025-03-25 00:00:00"), createdCoupon.getStartDate());
            assertEquals(localDateTimeConverter.toLocalDateTime("2025-03-30 23:59:59"), createdCoupon.getEndDate());
        }

        @Test
        public void 일반_사용자가_쿠폰_생성_시도_시_실패() throws Exception {
            // Given: 일반 사용자 토큰
            String userEncodedPassword = passwordEncoder.encode("!UserPassword1");
            User normalUser = User.toEntity("user@example.com", userEncodedPassword, "user", "userAddress", UserRole.USER);
            userRepository.save(normalUser);
            String userToken = jwtUtil.createToken(normalUser.getId(), normalUser.getUserRole());

            CouponCreateRequest request = new CouponCreateRequest(
                    "Invalid Coupon",
                    "Test Coupon Description",
                    2000,
                    5,
                    "2025-03-25 00:00:00",
                    "2025-03-30 23:59:59"
            );

            // When & Then
            mockMvc.perform(post("/api/v1/coupons")
                            .header("Authorization", userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("관리자 권한이 필요합니다.")));
        }

        @Test
        public void 유효하지_않은_데이터로_쿠폰_생성_시_실패() throws Exception {
            // Given: 유효하지 않은 데이터
            CouponCreateRequest request = new CouponCreateRequest(
                    "Invalid Coupon",
                    "Test Coupon Description",
                    null, // 유효하지 않은 값
                    5,
                    "2025-03-25 00:00:00",
                    "2025-03-30 23:59:59"
            );

            // When & Then
            mockMvc.perform(post("/api/v1/coupons")
                            .header("Authorization", adminToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("쿠폰 할인 가격은 필수값입니다.")));
        }
    }
}