package com.plusproject.domain.usercoupon.service;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.coupon.entity.Coupons;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.entity.Users;
import com.plusproject.domain.user.service.UserService;
import com.plusproject.domain.usercoupon.entity.UserCoupons;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserService userServ;
    private final CouponRepository couponRepo;
    private final UserCouponRepository userCouponRepo;

    public void couponIssue(Long userId) {
        Coupons coupon = couponRepo.findById(userId).orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_COUPON));
        Users user = userServ.findUser(userId);

        userCouponRepo.save(new UserCoupons(user, coupon));
    }
}
