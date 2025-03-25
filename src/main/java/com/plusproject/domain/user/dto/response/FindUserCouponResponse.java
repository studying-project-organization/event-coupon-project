package com.plusproject.domain.user.dto.response;

import com.plusproject.domain.coupon.enums.CouponStatus;
import com.plusproject.domain.usercoupon.entity.UserCoupons;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FindUserCouponResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final CouponStatus status;
    private final Integer discountAmount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static FindUserCouponResponse of(UserCoupons userCoupon) {
        return FindUserCouponResponse.builder()
            .id(userCoupon.getId())
            .name(userCoupon.getCoupon().getName())
            .description(userCoupon.getCoupon().getDescription())
            .discountAmount(userCoupon.getCoupon().getDiscountAmount())
            .status(userCoupon.getCouponStatus())
            .startDate(userCoupon.getCoupon().getStartDate())
            .endDate(userCoupon.getCoupon().getEndDate())
            .createdAt(userCoupon.getCreatedAt())
            .modifiedAt(userCoupon.getModifiedAt())
            .build();
    }

    @Builder
    private FindUserCouponResponse(Long id, String name, String description, CouponStatus status, Integer discountAmount, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt, LocalDateTime modifiedAt) {
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
