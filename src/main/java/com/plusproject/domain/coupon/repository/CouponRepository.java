package com.plusproject.domain.coupon.repository;

import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends BaseRepository<Coupon, Long> {

    @Modifying
    @Query("update Coupon c set c.quantity = c.quantity - 1 where c.id = :couponId and c.quantity > 0")
    void decrementQuantity(@Param("couponId") Long couponId);

}
