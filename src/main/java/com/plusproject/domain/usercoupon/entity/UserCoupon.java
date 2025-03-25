package com.plusproject.domain.usercoupon.entity;

import com.plusproject.common.entity.BaseEntity;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.usercoupon.enums.CouponStatusRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "UserCoupons")
public class UserCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    private CouponStatusRole couponStatusRole = CouponStatusRole.BEFORE_USE;

    public void setUser(User user) {
        this.user = user;
    }

    public void setCoupon(Coupon coupon) {
        this.coupon = coupon;
    }
}
