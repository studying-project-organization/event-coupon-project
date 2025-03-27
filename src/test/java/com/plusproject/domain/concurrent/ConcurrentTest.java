package com.plusproject.domain.concurrent;

import com.plusproject.SpringBootTestSupport;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import com.plusproject.domain.usercoupon.dto.request.IssuedCouponRequest;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class ConcurrentTest extends SpringBootTestSupport {

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
    @Transactional
    void setUp() {
        user = User.builder()
                .email("정청원12@naver.com")
                .password("Password1234!")
                .nickname("찌호")
                .address("서울")
                .userRole(UserRole.USER)
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

//        em.flush();

        authUser = AuthUser.builder()
                .id(user.getId())
                .userRole(user.getUserRole())
                .build();

        em.clear();
    }

    @Test
    public void 락없음() throws Exception {
        int testThread = 10;
        int testCount = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(testThread);
        CountDownLatch latch = new CountDownLatch(testCount);

        AtomicInteger successfulUpdates = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        IssuedCouponRequest request = IssuedCouponRequest.builder()
                .couponId(coupon.getId())
                .build();

        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    userCouponService.issuedCoupon(authUser, request);
                    successfulUpdates.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    System.out.println("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 완료할 때까지 대기
        executorService.shutdown();

        Coupon resultCoupon = couponRepository.findById(coupon.getId()).orElse(null);

        //재고를 10000개로 설정
        System.out.println("성공 카운트 : " + successfulUpdates);
        System.out.println("예외 카운트 : " + exceptionCount);
        System.out.println("실제 발급된 전체 쿠폰 개수 : " + userCouponRepository.count());
        System.out.println("쿠폰 테이블에서 예상한 발급된 쿠폰 개수 : " + (10000 - resultCoupon.getQuantity()));
    }
}
