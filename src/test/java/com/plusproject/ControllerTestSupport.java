package com.plusproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plusproject.common.auth.JwtUtil;
import com.plusproject.domain.auth.service.AuthService;
import com.plusproject.domain.coupon.service.CouponService;
import com.plusproject.domain.user.service.UserService;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtUtil jwtUtil;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected CouponService couponService;

    @MockitoBean
    protected UserCouponService userCouponService;

}
