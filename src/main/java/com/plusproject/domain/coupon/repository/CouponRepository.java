package com.plusproject.domain.coupon.repository;

import com.plusproject.common.exception.ErrorCode;
import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.coupon.entity.Coupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CouponRepository extends BaseRepository<Coupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Coupon findByIdWithLock(@Param("id") Long id, ErrorCode notFoundCoupon);
}
