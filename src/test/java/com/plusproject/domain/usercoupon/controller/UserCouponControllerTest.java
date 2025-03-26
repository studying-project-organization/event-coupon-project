package com.plusproject.domain.usercoupon.controller;

import com.plusproject.ControllerTestSupport;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.usercoupon.dto.request.IssuedCouponRequest;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserCouponControllerTest extends ControllerTestSupport {

    private static final String PATH = "/api/v1/user-coupon";

    private String accessToken;

    @BeforeEach
    void setUp() {
        accessToken = jwtUtil.createAccessToken(1L, UserRole.ADMIN);
    }

    @Test
    @DisplayName("쿠폰 발급하기 - 성공")
    void issuedCoupon1() throws Exception {
        // given
        IssuedCouponRequest request = IssuedCouponRequest.builder()
            .couponId(1L)
            .build();

        // when & then
        mockMvc.perform(post(PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("쿠폰 발급에 성공했습니다."));
    }

    @Test
    @DisplayName("쿠폰 발급하기 - DUPLICATE_COUPON_ISSUANCE 예외 발생")
    void issuedCoupon2() throws Exception {
        // given
        IssuedCouponRequest request = IssuedCouponRequest.builder()
            .couponId(1L)
            .build();

        doThrow(new ApplicationException(ErrorCode.DUPLICATE_COUPON_ISSUANCE))
            .when(userCouponService)
            .issuedCoupon(any(AuthUser.class), any(IssuedCouponRequest.class));

        // when & then
        mockMvc.perform(post(PATH)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_COUPON_ISSUANCE.getMessage()));
    }

//    @Test
//    @DisplayName("쿠폰 발급하기 - 동시성 예외")
//    void issuedCoupon3() throws Exception {
//        // given
//
//        // when
//
//        // then
//    }
    
    @Test
    @DisplayName("유저의 쿠폰 목록 조회 - 성공")
    void findAllUserCoupon1() throws Exception {
        // given & when & then
        mockMvc.perform(get(PATH)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, accessToken)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("쿠폰 조회에 성공했습니다."))
            .andExpect(jsonPath("$.data").isArray());
    }

}