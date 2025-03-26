package com.plusproject.domain.coupon.controller;

import com.plusproject.ControllerTestSupport;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import com.plusproject.domain.coupon.service.CouponService;
import com.plusproject.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CouponControllerTest extends ControllerTestSupport {

    private static final String PATH = "/api/v1/coupons";

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = jwtUtil.createAccessToken(1L, UserRole.ADMIN);
        userToken = jwtUtil.createAccessToken(2L, UserRole.USER);
    }

    @Test
    @DisplayName("쿠폰 생성하기 - 성공")
    void createCoupon1() throws Exception {
        // given
        CreateCouponRequest request = CreateCouponRequest.builder()
            .name("쿠폰 이름")
            .description("쿠폰 설명")
            .discountAmount(10000)
            .quantity(50000)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(7))
            .build();

        // when & then
        mockMvc.perform(post(PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, adminToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("쿠폰이 생성되었습니다."));
    }

    @Test
    @DisplayName("쿠폰 생성하기 - FORBIDDEN_ADMIN_ONLY 예외 발생")
    void createCoupon2() throws Exception {
        // given
        CreateCouponRequest request = CreateCouponRequest.builder()
            .name("쿠폰 이름")
            .description("쿠폰 설명")
            .discountAmount(10000)
            .quantity(50000)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(7))
            .build();

        // when & then
        mockMvc.perform(post(PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, userToken)
            )
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.message").value(ErrorCode.FORBIDDEN_ADMIN_ONLY.getMessage()));
    }

}