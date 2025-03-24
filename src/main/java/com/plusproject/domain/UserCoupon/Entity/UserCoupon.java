package com.plusproject.domain.UserCoupon.Entity;

import com.plusproject.common.entity.BaseEntity;
import com.plusproject.domain.UserCoupon.enums.CouponStatus;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "user_coupons")
public class UserCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupon_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;

    private UserCoupon(User user, Coupon coupon, CouponStatus couponStatus) {
        this.user = user;
        this.coupon = coupon;
        this.couponStatus = couponStatus;
    }

    public static UserCoupon toEntity( User user, Coupon coupon, CouponStatus couponStatus){
        return new UserCoupon(user, coupon, couponStatus);
    }
}
