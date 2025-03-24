package com.plusproject.domain.usercoupon.repository;

import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.usercoupon.entity.UserCoupon;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserCouponRepository extends BaseRepository<UserCoupon, Long> {

    @Query("select uc from UserCoupon uc join fetch uc.coupon c where uc.user.id = :userId")
    List<UserCoupon> findAllByUser_Id(@Param("userId") Long userId);

    boolean existsByUser_IdAndCoupon_Id(Long userId, Long couponId);

}
