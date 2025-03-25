package com.plusproject.domain.usercoupon.entity;

import com.plusproject.common.entity.BaseEntity;
import com.plusproject.domain.coupon.entity.Coupons;
import com.plusproject.domain.user.entity.Users;
import com.plusproject.domain.coupon.enums.CouponStatus;
import com.plusproject.domain.usercoupon.dto.request.CouponIssueRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupons extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_coupons_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupons coupon;

    private CouponStatus couponStatus = CouponStatus.UNUSED;

    public UserCoupons(Users user, Coupons coupon) {
        this.user = user;
        this.coupon = coupon;
    }
}
