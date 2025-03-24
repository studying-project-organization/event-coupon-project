package com.plusproject.domain.UserCoupon.repository;

import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.UserCoupon.Entity.UserCoupon;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;

public interface UserCouponRepository extends BaseRepository<UserCoupon, Long> {

    boolean existsByIdAndUserId(Long couponId, Long userId);

    @EntityGraph(
            attributePaths = {
                    "user",
                    "coupon"
            }
    )
    List<UserCoupon> findAllByUserId(Long userId);
}
