package com.plusproject.domain.coupon.service;

import com.plusproject.config.LocalDateTimeConverter;
import com.plusproject.domain.coupon.dto.request.CouponCreateRequest;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final LocalDateTimeConverter localDateTimeConverter;

    @Transactional
    public void createCoupon(CouponCreateRequest request) {
        Coupon coupon = Coupon.toEntity(
                request.getName(),
                request.getDescription(),
                request.getDiscountAmount(),
                request.getQuantity(),
                localDateTimeConverter.toLocalDateTime(request.getStartDate()),
                localDateTimeConverter.toLocalDateTime(request.getEndDate()));

        couponRepository.save(coupon);
    }
}
