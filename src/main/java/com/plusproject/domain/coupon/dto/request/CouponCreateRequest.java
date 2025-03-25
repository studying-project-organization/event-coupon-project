package com.plusproject.domain.coupon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateRequest {
    private String name;
    private String description;
    private Integer discountAmount;
    private Integer quantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
