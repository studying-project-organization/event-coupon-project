package com.plusproject.domain.usercoupon.service;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.repository.UserRepository;
import com.plusproject.domain.usercoupon.dto.request.UserCouponeGetRequest;
import com.plusproject.domain.usercoupon.dto.response.UserCouponResponse;
import com.plusproject.domain.usercoupon.entity.UserCoupon;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCouponService {
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    public UserCouponResponse getUserCoupon(Long userId, UserCouponeGetRequest userCouponeGetRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_USER));

        Coupon coupon = couponRepository.findById(userCouponeGetRequest.getCouponId())
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_COUPON));

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUser(user);
        userCoupon.setCoupon(coupon);
        userCouponRepository.save(userCoupon);

        return new UserCouponResponse(
                userCoupon.getId(),
                userCoupon.getUser().getId(),
                userCoupon.getCoupon().getId(),
                userCoupon.getCouponStatusRole().name(),
                userCoupon.getCreatedAt(),
                userCoupon.getModifiedAt()
        );
    }

    public List<UserCouponResponse> getUserCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserId(userId);
        List<UserCouponResponse> userCouponResponses = new ArrayList<>();

        for (UserCoupon userCoupon : userCoupons) {
            UserCouponResponse response = new UserCouponResponse(
                    userCoupon.getId(),
                    userCoupon.getUser().getId(),
                    userCoupon.getCoupon().getId(),
                    userCoupon.getCouponStatusRole().name(),
                    userCoupon.getCreatedAt(),
                    userCoupon.getModifiedAt()
            );
            userCouponResponses.add(response);
        }

        return userCouponResponses;
    }
}
