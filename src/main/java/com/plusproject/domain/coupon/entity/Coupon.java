package com.plusproject.domain.coupon.entity;

import com.plusproject.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;
    private String name;
    private String description;
    private Integer discountAmount;
    private Integer quantity;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public Coupon(String name, String description, Integer discountAmount, Integer quantity, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.description = description;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
