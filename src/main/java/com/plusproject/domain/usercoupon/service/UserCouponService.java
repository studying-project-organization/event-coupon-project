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
import com.plusproject.redis.LettuceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.plusproject.common.exception.ErrorCode.NOT_FOUND_COUPON;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final LettuceRepository lockServ;

    @Transactional
    public Long issuedCoupon(AuthUser authUser, IssuedCouponRequest request) throws InterruptedException {
        String lockKey = "usercoupon_" + request.getCouponId() + "_lock";
        String lockValue = UUID.randomUUID().toString();

//        if (!lockServ.acquireLock(lockKey, lockValue, 5)) {
//            throw new ApplicationException(ErrorCode.REQUEST_TIMEOUT, "다른 요청이 너무 오래 걸리고 있습니다.");
//        }

        try {
            User findUser = userRepository.findByIdOrElseThrow(authUser.getId(), ErrorCode.NOT_FOUND_USER);
            Coupon findCoupon = couponRepository.findByIdOrElseThrow(request.getCouponId(), NOT_FOUND_COUPON);

//            if (userCouponRepository.existsByUser_IdAndCoupon_Id(findUser.getId(), findCoupon.getId())) {
//                throw new ApplicationException(ErrorCode.DUPLICATE_COUPON_ISSUANCE);
//            }

            UserCoupon newUserCoupon = UserCoupon.builder()
                    .user(findUser)
                    .coupon(findCoupon)
                    .status(CouponStatus.ISSUED)
                    .build();

            lockServ.acquireLock(lockKey, lockValue, 5);

            findCoupon.decreaseQuantity();
            UserCoupon savedUserCoupon = userCouponRepository.save(newUserCoupon);
            return savedUserCoupon.getId();
        }
        finally {
            lockServ.releaseLock(lockKey, lockValue);
        }
    }

    public List<UserCouponResponse> findAllUserCoupon(AuthUser authUser) {
        return userCouponRepository.findAllByUser_Id(authUser.getId())
            .stream()
            .map(UserCouponResponse::of)
            .toList();
    }
}
