package com.plusproject.domain.UserCoupon.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.JwtUtil;
import com.plusproject.config.LocalDateTimeConverter;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.UserCoupon.Entity.UserCoupon;
import com.plusproject.domain.UserCoupon.enums.CouponStatus;
import com.plusproject.domain.UserCoupon.repository.UserCouponRepository;
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
class UserCouponControllerTest {

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


    @Nested
    class 쿠폰_발급_관련_테스트 {
        private String token;
        private User testUser;
        private Coupon testCoupon;

        @BeforeEach
        public void setUp() {
            userCouponRepository.deleteAll();
            userRepository.deleteAll();
            couponRepository.deleteAll();

            // 테스트용 사용자 생성
            String encodedPassword = passwordEncoder.encode("!Password1");
            testUser = User.toEntity("test@example.com", encodedPassword, "nickname", "address", UserRole.USER);
            userRepository.save(testUser);

            // JWT 토큰 생성
            token = jwtUtil.createToken(testUser.getId(), testUser.getUserRole());

            // 테스트용 쿠폰 생성
            testCoupon = Coupon.toEntity("Test Coupon", "Test Description", 1000, 10,
                    localDateTimeConverter.toLocalDateTime("2025-03-24 00:00:00"),
                    localDateTimeConverter.toLocalDateTime("2025-03-28 23:59:59"));
            couponRepository.save(testCoupon);
        }

        @Test
        public void 쿠폰_발급_성공() throws Exception {
            // When & Then
            mockMvc.perform(post("/api/v1/users/coupons/" + testCoupon.getId())
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("쿠폰 발급에 성공했습니다.")));

            // DB에서 쿠폰 발급 확인
            UserCoupon userCoupon = userCouponRepository.findByIdAndUserId(testCoupon.getId(), testUser.getId());
            assertEquals(CouponStatus.RECEIVE, userCoupon.getCouponStatus());
            assertEquals(9, couponRepository.findById(testCoupon.getId()).get().getQuantity()); // 수량 감소 확인
        }

        @Test
        public void 이미_발급된_쿠폰_재발급_시도_시_실패() throws Exception {
            // Given: 이미 발급된 쿠폰
            UserCoupon userCoupon = UserCoupon.toEntity(testUser, testCoupon, CouponStatus.RECEIVE);
            userCouponRepository.save(userCoupon);

            // When & Then
            mockMvc.perform(post("/api/v1/users/coupons/" + testCoupon.getId())
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString(ErrorCode.BAD_REQUEST_ALREADY_HAVE_COUPON.getMessage())));
        }

        @Test
        public void 쿠폰_조회_성공() throws Exception {
            // Given: 발급된 쿠폰 추가
            UserCoupon userCoupon = UserCoupon.toEntity(testUser, testCoupon, CouponStatus.RECEIVE);
            userCouponRepository.save(userCoupon);

            // When & Then
            mockMvc.perform(get("/api/v1/users/coupons")
                            .header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("쿠폰 조회에 성공했습니다.")))
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Coupon")))
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("1000")));
        }
    }
}