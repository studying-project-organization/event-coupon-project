package com.plusproject.domain.usercoupon.service;

import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.common.lock.DistributedLockManager;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.enums.CouponStatus;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.repository.UserRepository;
import com.plusproject.domain.usercoupon.dto.request.IssuedCouponRequest;
import com.plusproject.domain.usercoupon.dto.response.UserCouponResponse;
import com.plusproject.domain.usercoupon.entity.UserCoupon;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final DistributedLockManager lockManager;

    @Transactional
    public Long issuedCoupon(AuthUser authUser, IssuedCouponRequest request) {

        try {
            AtomicReference<Long> userCouponId = new AtomicReference<>();
            lockManager.executeWithLock(request.getCouponId(), () -> {

                User findUser = userRepository.findByIdOrElseThrow(authUser.getId(), ErrorCode.NOT_FOUND_USER);
                Coupon findCoupon = couponRepository.findByIdOrElseThrow(request.getCouponId(), ErrorCode.NOT_FOUND_COUPON);

//        if (userCouponRepository.existsByUser_IdAndCoupon_Id(findUser.getId(), findCoupon.getId())) {
//            throw new ApplicationException(ErrorCode.DUPLICATE_COUPON_ISSUANCE);
//        }

                if (findCoupon.getQuantity() <= 0) {
                    throw new ApplicationException(ErrorCode.EXHAUSETD_COUPON);
                }

                couponRepository.decrementQuantity(request.getCouponId());

                UserCoupon newUserCoupon = UserCoupon.builder()
                    .user(findUser)
                    .coupon(findCoupon)
                    .status(CouponStatus.ISSUED)
                    .build();

                userCouponId.set(userCouponRepository.save(newUserCoupon).getId());
            });

            return userCouponId.get();
        } catch (InterruptedException ex) {
            throw new ApplicationException(ErrorCode.ACQUISITION_FAILED_LOCK);
        }
    }

    public List<UserCouponResponse> findAllUserCoupon(AuthUser authUser) {
        return userCouponRepository.findAllByUser_Id(authUser.getId())
            .stream()
            .map(UserCouponResponse::of)
            .toList();
    }
}
