package com.plusproject.domain.coupon.service;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import com.plusproject.domain.coupon.entity.Coupons;
import com.plusproject.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepo;

    public void createCoupon(CreateCouponRequest req) {
        Coupons coupons = new Coupons(req);
        couponRepo.save(coupons);
    }
}
