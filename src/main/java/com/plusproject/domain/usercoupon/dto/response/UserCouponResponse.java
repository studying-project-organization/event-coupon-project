package com.plusproject.domain.usercoupon.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCouponResponse {
    private final Long id;
    private final Long userId;
    private final Long couponId;
    private final String status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public UserCouponResponse(Long id, Long userId, Long couponId, String status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
