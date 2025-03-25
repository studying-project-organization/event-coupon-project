package com.plusproject.domain.coupon.entity;

import com.plusproject.common.entity.BaseEntity;
import com.plusproject.domain.coupon.dto.request.CreateCouponRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupons extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupons_id")
    private Long id;

    private String name;

    private String description;

    private int discountAmount;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public Coupons(CreateCouponRequest req) {
        this.description = req.getDescription();
        this.discountAmount = req.getDiscountAmount();
        this.name = req.getName();
        this.endDate = req.getEndDate();
        this.startDate = req.getStartDate();
    }
}