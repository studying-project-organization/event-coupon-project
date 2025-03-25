package com.plusproject.domain.coupon.service;

import com.plusproject.common.dto.ApiResponse;
import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import com.plusproject.domain.coupon.entity.Coupons;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepo;
    private final UserCouponRepository UCRepo;

    public ApiResponse<Void> createCoupon(CreateCouponRequest req) {
        Coupons coupons = new Coupons(req);
        couponRepo.save(coupons);

        return ApiResponse.success("쿠폰이 생성되었습니다.");
    }


}
