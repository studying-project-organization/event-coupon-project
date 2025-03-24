package com.plusproject.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserCouponInfoResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Integer discountAmount;
    private final String startDate;
    private final String endDate;

    private UserCouponInfoResponse(Long id, String name, String description, Integer discountAmount, String startDate,
                                   String endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.discountAmount = discountAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static UserCouponInfoResponse of(Long id, String name, String description, Integer discountAmount,
                                            String startDate,
                                            String endDate) {
        return new UserCouponInfoResponse(id, name, description, discountAmount, startDate, endDate);
    }
}
