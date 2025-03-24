package com.plusproject.domain.coupon.entity;

import com.plusproject.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;


    private Coupon(String name, String description, Integer discountAmount, Integer quantity,
                  LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.description = description;
        this.discountAmount = discountAmount;
        this.quantity = quantity;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Coupon toEntity(String name, String description, Integer discountAmount, Integer quantity,
                                  LocalDateTime startDate, LocalDateTime endDate){
        return new Coupon(name, description, discountAmount, quantity, startDate, endDate);
    }
}
