package com.plusproject.domain.coupon.service;

import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public Long createCoupon(CreateCouponRequest request) {
        Coupon newCoupon = Coupon.builder()
            .name(request.getName())
            .description(request.getDescription())
            .discountAmount(request.getDiscountAmount())
            .quantity(request.getQuantity())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .build();

        Coupon savedCoupon = couponRepository.save(newCoupon);
        return savedCoupon.getId();
    }

}
