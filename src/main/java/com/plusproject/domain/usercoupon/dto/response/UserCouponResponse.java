package com.plusproject.domain.usercoupon.dto.response;

import com.plusproject.domain.coupon.enums.CouponStatus;
import com.plusproject.domain.usercoupon.entity.UserCoupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCouponResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final CouponStatus status;
    private final Integer discountAmount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static UserCouponResponse of(UserCoupon userCoupon) {
        return UserCouponResponse.builder()
            .id(userCoupon.getId())
            .name(userCoupon.getCoupon().getName())
            .description(userCoupon.getCoupon().getDescription())
            .discountAmount(userCoupon.getCoupon().getDiscountAmount())
            .status(userCoupon.getStatus())
            .startDate(userCoupon.getCoupon().getStartDate())
            .endDate(userCoupon.getCoupon().getEndDate())
            .createdAt(userCoupon.getCreatedAt())
            .modifiedAt(userCoupon.getModifiedAt())
            .build();
    }

    @Builder
    private UserCouponResponse(Long id, String name, String description, CouponStatus status, Integer discountAmount, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.discountAmount = discountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
