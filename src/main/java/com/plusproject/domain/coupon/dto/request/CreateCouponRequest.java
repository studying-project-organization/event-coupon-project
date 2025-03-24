package com.plusproject.domain.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CreateCouponRequest {

    @NotBlank(message = "쿠폰 이름 입력은 필수입니다.")
    private String name;

    @NotBlank(message = "쿠폰 내용 입력은 필수입니다.")
    private String description;

    @NotNull(message = "할인 금액 입력은 필수입니다.")
    private Integer discountAmount;

    @NotNull(message = "쿠폰 수량 입력은 필수입니다.")
    private Integer quantity;

    @NotNull(message = "쿠폰 시작일 입력은 필수입니다.")
    private LocalDateTime startDate;

    @NotNull(message = "쿠폰 종료일 입력은 필수입니다.")
    private LocalDateTime endDate;

}
