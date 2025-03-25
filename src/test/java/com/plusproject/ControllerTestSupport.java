package com.plusproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plusproject.common.auth.JwtUtil;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.auth.controller.AuthController;
import com.plusproject.domain.auth.service.AuthService;
import com.plusproject.domain.coupon.controller.CouponController;
import com.plusproject.domain.coupon.service.CouponService;
import com.plusproject.domain.user.controller.UserController;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.service.UserService;
import com.plusproject.domain.usercoupon.controller.UserCouponController;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
    AuthController.class,
    UserController.class,
    CouponController.class,
    UserCouponController.class
})
public class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected AuthService authService;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected CouponService couponService;

    @MockitoBean
    protected UserCouponService userCouponService;

}
