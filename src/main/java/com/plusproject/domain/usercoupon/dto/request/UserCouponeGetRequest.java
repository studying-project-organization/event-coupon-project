package com.plusproject.domain.usercoupon.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCouponeGetRequest {
    private Long userId;
    private Long couponId;
}
