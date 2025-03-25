package com.plusproject.domain.usercoupon.repository;

import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.usercoupon.entity.UserCoupon;

import java.util.List;

public interface UserCouponRepository extends BaseRepository<UserCoupon, Long> {
    List<UserCoupon> findByUserId(Long userId);
}
