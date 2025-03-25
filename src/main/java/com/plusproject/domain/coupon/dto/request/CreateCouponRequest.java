package com.plusproject.domain.coupon.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CreateCouponRequest {
    private String name;
    private String description;
    private int discountAmount;
    private int quantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
