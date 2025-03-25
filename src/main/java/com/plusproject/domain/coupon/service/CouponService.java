package com.plusproject.domain.coupon.service;

import com.plusproject.domain.coupon.dto.request.CouponCreateRequest;
import com.plusproject.domain.coupon.dto.response.CouponResponse;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    @Transactional
    public CouponResponse createCoupon(CouponCreateRequest couponCreateRequest) {
        Coupon newCoupon = new Coupon(
                couponCreateRequest.getName(),
                couponCreateRequest.getDescription(),
                couponCreateRequest.getDiscountAmount(),
                couponCreateRequest.getQuantity(),
                couponCreateRequest.getStartDate(),
                couponCreateRequest.getEndDate()
        );
        couponRepository.save(newCoupon);

        return new CouponResponse(
                newCoupon.getId(),
                newCoupon.getName(),
                newCoupon.getDescription(),
                newCoupon.getDiscountAmount(),
                newCoupon.getQuantity(),
                newCoupon.getStartDate(),
                newCoupon.getEndDate(),
                newCoupon.getCreatedAt(),
                newCoupon.getModifiedAt()
        );
    }
}
