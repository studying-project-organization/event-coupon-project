package com.plusproject.domain.usercoupon.repository;

import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.coupon.entity.Coupons;
import com.plusproject.domain.usercoupon.entity.UserCoupons;

import java.util.List;

public interface UserCouponRepository extends BaseRepository<UserCoupons, Long> {

    List<UserCoupons> findByUser_Id(Long userId);
}
