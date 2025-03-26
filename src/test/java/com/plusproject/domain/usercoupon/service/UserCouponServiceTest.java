package com.plusproject.domain.usercoupon.service;

import com.plusproject.SpringBootTestSupport;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.enums.CouponStatus;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import com.plusproject.domain.usercoupon.dto.request.IssuedCouponRequest;
import com.plusproject.domain.usercoupon.dto.response.UserCouponResponse;
import com.plusproject.domain.usercoupon.entity.UserCoupon;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@Transactional
class UserCouponServiceTest extends SpringBootTestSupport {

    private static final String NOT_FOUND_VALUE = " id = -1";

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private EntityManager em;

    private User user;

    private AuthUser authUser;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("test@example.com")
            .password("Password1234!")
            .nickname("찌호")
            .address("서울")
            .userRole(UserRole.ADMIN)
            .build();

        coupon = Coupon.builder()
            .name("쿠폰 이름")
            .description("쿠폰 설명")
            .discountAmount(1000)
            .quantity(10000)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusDays(7))
            .build();

        userRepository.save(user);
        couponRepository.save(coupon);

        em.flush();

        authUser = AuthUser.builder()
            .id(user.getId())
            .userRole(user.getUserRole())
            .build();

        em.clear();
    }

    @Test
    @DisplayName("쿠폰 발급받기 - 성공")
    void issuedCoupon1() throws Exception {
        // given
        IssuedCouponRequest request = IssuedCouponRequest.builder()
            .couponId(coupon.getId())
            .build();

        // when
        Long userCouponId = userCouponService.issuedCoupon(authUser, request);
        UserCoupon findUserCoupon = userCouponRepository.findByIdOrElseThrow(userCouponId, ErrorCode.NOT_FOUND_USER_COUPON);

        // then
        assertThat(findUserCoupon)
            .extracting("id", "status")
            .containsExactly(userCouponId, CouponStatus.ISSUED);

        assertThat(findUserCoupon.getUser())
            .extracting("id", "email", "nickname", "userRole")
            .containsExactly(user.getId(), user.getEmail(), user.getNickname(), user.getUserRole());

        assertThat(findUserCoupon.getCoupon())
            .extracting("id", "name", "description", "discountAmount", "quantity")
            .containsExactly(
                coupon.getId(),
                coupon.getName(),
                coupon.getDescription(),
                coupon.getDiscountAmount(),
                coupon.getQuantity()
            );
    }

    @Test
    @DisplayName("쿠폰 발급받기시에 유저가 존재하지 않으면 NOT_FOUND_USER 예외 발생")
    void issuedCoupon2() throws Exception {
        // given
        AuthUser testUser = AuthUser.builder()
            .id(-1L)
            .userRole(UserRole.ADMIN)
            .build();

        IssuedCouponRequest request = IssuedCouponRequest.builder()
            .couponId(coupon.getId())
            .build();

        // when & then
        assertThatThrownBy(() -> userCouponService.issuedCoupon(testUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage() + NOT_FOUND_VALUE);
    }

    @Test
    @DisplayName("쿠폰 발급받기시에 쿠폰이 존재하지 않으면 NOT_FOUND_COUPON 예외 발생")
    void issuedCoupon3() throws Exception {
        // given
        IssuedCouponRequest request = IssuedCouponRequest.builder()
            .couponId(-1L)
            .build();

        // when & then
        assertThatThrownBy(() -> userCouponService.issuedCoupon(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_COUPON.getMessage() + NOT_FOUND_VALUE);
    }

    @Test
    @DisplayName("쿠폰 발급받기시에 이미 발급받은 쿠폰이라면 DUPLICATE_COUPON_ISSUANCE 예외 발생")
    void issuedCoupon4() throws Exception {
        // given
        IssuedCouponRequest request = IssuedCouponRequest.builder()
            .couponId(coupon.getId())
            .build();

        userCouponService.issuedCoupon(authUser, request);

        // when & then
        assertThatThrownBy(() -> userCouponService.issuedCoupon(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.DUPLICATE_COUPON_ISSUANCE.getMessage());
    }

    @Test
    @DisplayName("유저의 쿠폰 목록 조회 - 성공(0개의 데이터)")
    void findAllUserCoupon1() throws Exception {
        // when
        List<UserCouponResponse> result = userCouponService.findAllUserCoupon(authUser);

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    @DisplayName("유저의 쿠폰 목록 조회 - 성공(2개의 데이터)")
    void findAllUserCoupon2() throws Exception {
        // given
        UserCoupon userCoupon1 = UserCoupon.builder()
            .user(user)
            .coupon(coupon)
            .status(CouponStatus.ISSUED)
            .build();

        UserCoupon userCoupon2 = UserCoupon.builder()
            .user(user)
            .coupon(coupon)
            .status(CouponStatus.USED)
            .build();

        userCouponRepository.saveAll(List.of(userCoupon1, userCoupon2));

        // when
        List<UserCouponResponse> result = userCouponService.findAllUserCoupon(authUser);

        // then
        assertThat(result).hasSize(2)
            .extracting("id", "status")
            .containsExactly(
                tuple(result.get(0).getId(), result.get(0).getStatus()),
                tuple(result.get(1).getId(), result.get(1).getStatus())
            );

    }
}