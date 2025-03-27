package com.plusproject.domain.coupon.repository;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.coupon.entity.Coupon;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CouponRepository extends BaseRepository<Coupon, Long> {

    @Modifying
    @Query("update Coupon c set c.quantity = c.quantity - 1 where c.id = :couponId and c.quantity > 0")
    int decrementQuantity(@Param("couponId") Long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)       // 이렇게 사용하면 FOR UPDATE 를 붙여준다고 한다.
    @Query("select c from Coupon c where c.id = :couponId")
    Optional<Coupon> findByIdForUpdate(@Param("couponId") Long couponId);

    default Coupon findByIdForUpdateOrElseThrow(Long couponId) {
        return findByIdForUpdate(couponId)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_COUPON));
    }

}
