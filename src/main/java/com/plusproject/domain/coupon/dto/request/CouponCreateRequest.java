package com.plusproject.domain.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponCreateRequest {

    @NotBlank(message = "쿠폰 이름은 필수값입니다.")
    private String name;

    @NotBlank(message = "쿠폰 설명은 필수값입니다.")
    private String description;

    @NotNull(message = "쿠폰 할인 가격은 필수값입니다.")
    @Positive(message = "쿠폰 할인 가격은 양수여야 합니다.")
    private Integer discountAmount;

    @NotNull(message = "쿠폰 수량은 필수값입니다.")
    @Positive(message = "쿠폰 수량은 양수여야 합니다.")
    private Integer quantity;

    @NotBlank(message = "쿠폰 시작일은 필수값입니다.")
    private String startDate;

    @NotBlank(message = "쿠폰 만료일은 필수값입니다.")
    private String endDate;
}
