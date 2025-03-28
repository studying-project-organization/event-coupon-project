package com.plusproject.domain.usercoupon.service;

import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.common.redis.service.LockService;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final LockService lockService;

//    // 비관적 락처리
//    @Transactional
//    public Long issuedCoupon(AuthUser authUser, IssuedCouponRequest request) {
//        User findUser = userRepository.findByIdOrElseThrow(authUser.getId(), ErrorCode.NOT_FOUND_USER);
//        Coupon findCoupon = couponRepository.findByIdWithLock(request.getCouponId(), ErrorCode.NOT_FOUND_COUPON);
//

    /// /        if (userCouponRepository.existsByUser_IdAndCoupon_Id(findUser.getId(), findCoupon.getId())) {
    /// /            throw new ApplicationException(ErrorCode.DUPLICATE_COUPON_ISSUANCE);
    /// /        }
//
//        if (findCoupon.getQuantity() <= 0) {
//            throw new ApplicationException(ErrorCode.SOLDOUT_COUPON);
//        }
//
//        findCoupon.setQuantity(findCoupon.getQuantity() - 1);
//        couponRepository.save(findCoupon); // 변경된 쿠폰 수량 저장
//
//        UserCoupon newUserCoupon = UserCoupon.builder()
//            .user(findUser)
//            .coupon(findCoupon)
//            .status(CouponStatus.ISSUED)
//            .build();
//
//        UserCoupon savedUserCoupon = userCouponRepository.save(newUserCoupon);
//        return savedUserCoupon.getId();
//    }

    @Transactional
    public Long issuedCoupon(AuthUser authUser, IssuedCouponRequest request) {
        User findUser = userRepository.findByIdOrElseThrow(authUser.getId(), ErrorCode.NOT_FOUND_USER);
        Coupon findCoupon = couponRepository.findByIdOrElseThrow(request.getCouponId(), ErrorCode.NOT_FOUND_COUPON);

        String lockKey = "coupon_lock:" + request.getCouponId();
        String lockValue;

        try {
            lockValue = lockService.lock(lockKey, 30); // 30초 동안 락 획득
        } catch (Exception e) {
            log.error("락 획득 중 예외 발생: {}", e.getMessage(), e);
            throw new ApplicationException(ErrorCode.LOCK_ACQUISITION_FAILED);
        }

        try {
            // 동시성 제어 로직
            if (findCoupon.getQuantity() <= 0) {
                throw new ApplicationException(ErrorCode.SOLDOUT_COUPON);
            }

            findCoupon.setQuantity(findCoupon.getQuantity() - 1);
            couponRepository.save(findCoupon); // 변경된 쿠폰 수량 저장

            UserCoupon newUserCoupon = UserCoupon.builder()
                    .user(findUser)
                    .coupon(findCoupon)
                    .status(CouponStatus.ISSUED)
                    .build();

            UserCoupon savedUserCoupon = userCouponRepository.save(newUserCoupon);
            return savedUserCoupon.getId();
        } finally {
            lockService.unlock(lockKey, lockValue); // 락 해제
        }
    }

    @Transactional
    public List<UserCouponResponse> findAllUserCoupon(AuthUser authUser) {
        return userCouponRepository.findAllByUser_Id(authUser.getId())
                .stream()
                .map(UserCouponResponse::of)
                .toList();
    }
}
