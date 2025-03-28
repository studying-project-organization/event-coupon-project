package com.plusproject.domain.usercoupon.service;

import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.enums.CouponStatus;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.repository.UserRepository;
import com.plusproject.domain.usercoupon.dto.request.IssuedCouponRequest;
import com.plusproject.domain.usercoupon.dto.response.UserCouponResponse;
import com.plusproject.domain.usercoupon.entity.UserCoupon;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import com.plusproject.redis.DistributedLockManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;


import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


import static com.plusproject.common.exception.ErrorCode.EXHAUSETD_COUPON;
import static com.plusproject.common.exception.ErrorCode.NOT_FOUND_COUPON;

@Transactional(readOnly = true)
@Service
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    private final DistributedLockManager lockManager;

    public UserCouponService(
            UserCouponRepository userCouponRepository,
            UserRepository userRepository,
            CouponRepository couponRepository,
            @Qualifier("redissonLock") DistributedLockManager lockManager
    ) {
        this.userCouponRepository = userCouponRepository;
        this.userRepository = userRepository;
        this.couponRepository = couponRepository;
        this.lockManager = lockManager;
    }

    @Transactional
    public Long issuedCoupon(AuthUser authUser, IssuedCouponRequest request) throws InterruptedException {
        try {
            AtomicReference<Long> userCouponId = new AtomicReference<>();
            lockManager.executeWithLock(request.getCouponId(), () -> {
                User findUser = userRepository.findByIdOrElseThrow(authUser.getId(), ErrorCode.NOT_FOUND_USER);
                Coupon findCoupon = couponRepository.findByIdOrElseThrow(request.getCouponId(), NOT_FOUND_COUPON);
                int quantityResult = couponRepository.decrementQuantity(request.getCouponId());
                if (quantityResult == 0) {
                    throw new ApplicationException(EXHAUSETD_COUPON);
                }

                UserCoupon newUserCoupon = UserCoupon.builder()
                        .user(findUser)
                        .coupon(findCoupon)
                        .status(CouponStatus.ISSUED)
                        .build();

                userCouponId.set(userCouponRepository.save(newUserCoupon).getId());
            });

            return userCouponId.get();
        } catch (InterruptedException e) {
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
