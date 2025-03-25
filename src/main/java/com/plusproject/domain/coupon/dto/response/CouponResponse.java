package com.plusproject.domain.coupon.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final Integer discountAmount;
    private final Integer quantity;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public CouponResponse(Long id, String name, String description, Integer discountAmount, Integer quantity, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
