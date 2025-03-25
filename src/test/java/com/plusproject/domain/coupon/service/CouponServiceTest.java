package com.plusproject.domain.coupon.service;

import com.plusproject.SpringBootTestSupport;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
class CouponServiceTest extends SpringBootTestSupport {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰 생성하기 - 성공")
    void createCoupon1() throws Exception {
        // given
        LocalDateTime now = LocalDateTime.now();

        CreateCouponRequest request = CreateCouponRequest.builder()
            .name("쿠폰 이름")
            .description("쿠폰 설명")
            .discountAmount(1000)
            .quantity(50000)
            .startDate(now)
            .endDate(now.plusDays(7))
            .build();

        // when
        Long couponId = couponService.createCoupon(request);
        Coupon findCoupon = couponRepository.findByIdOrElseThrow(couponId, ErrorCode.NOT_FOUND_COUPON);

        // then
        Assertions.assertThat(findCoupon)
            .extracting("id", "name", "description", "discountAmount", "quantity", "startDate", "endDate")
            .containsExactly(
                couponId,
                request.getName(),
                request.getDescription(),
                request.getDiscountAmount(),
                request.getQuantity(),
                request.getStartDate(),
                request.getEndDate()
            );
    }
}