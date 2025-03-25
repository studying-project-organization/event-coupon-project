package com.plusproject.domain.user.service;

import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.LocalDateTimeConverter;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.UserCoupon.Entity.UserCoupon;
import com.plusproject.domain.UserCoupon.enums.CouponStatus;
import com.plusproject.domain.UserCoupon.repository.UserCouponRepository;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.dto.request.UserPasswordUpdateRequest;
import com.plusproject.domain.user.dto.response.UserCouponInfoResponse;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserCouponRepository userCouponRepository;
    private final CouponRepository couponRepository;
    private final LocalDateTimeConverter localDateTimeConverter;

    @Transactional
    public void updatePassword(AuthUser authUser, @Valid UserPasswordUpdateRequest request) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST_DO_NOT_MATCH_OLD_AND_NEW_PASSWORD);
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST_NOT_MATCH_OLD_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    public void receiveCoupon(AuthUser authUser, Long couponId) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);
        Coupon coupon = couponRepository.findByIdOrElseThrow(couponId, ErrorCode.NOT_FOUND_COUPON);

        if (userCouponRepository.existsByIdAndUserId(couponId, user.getId())) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST_ALREADY_HAVE_COUPON);
        }

        if (coupon.getQuantity() == 0) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST_QUANTITY_IS_ZERO);
        }

        coupon.consume();
        UserCoupon userCoupon = UserCoupon.toEntity(user, coupon, CouponStatus.RECEIVE);
        userCouponRepository.save(userCoupon);
    }


    public List<UserCouponInfoResponse> findCoupons(AuthUser authUser) {
        User user = userRepository.findByIdOrElseThrow(authUser.getUserId(), ErrorCode.NOT_FOUND_USER);

        List<UserCoupon> userCoupons = userCouponRepository.findAllByUserId(user.getId());
        return userCoupons.stream().map(
                userCoupon -> {
                    Coupon coupon = userCoupon.getCoupon();
                    return UserCouponInfoResponse.of(
                            coupon.getId(),
                            coupon.getName(),
                            coupon.getDescription(),
                            coupon.getDiscountAmount(),
                            localDateTimeConverter.toString(coupon.getStartDate()),
                            localDateTimeConverter.toString(coupon.getEndDate())
                    );
                }
        ).toList();
    }
}
