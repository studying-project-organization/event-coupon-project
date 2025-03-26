package com.plusproject.load;

import com.plusproject.SpringBootTestSupport;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.config.LocalDateTimeConverter;
import com.plusproject.domain.coupon.entity.Coupon;
import com.plusproject.domain.coupon.repository.CouponRepository;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import com.plusproject.domain.usercoupon.dto.request.IssuedCouponRequest;
import com.plusproject.domain.usercoupon.repository.UserCouponRepository;
import com.plusproject.domain.usercoupon.service.UserCouponService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
public class ConcurrencyTest extends SpringBootTestSupport {

    private static final Logger log = LogManager.getLogger(ConcurrencyTest.class);
    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private LocalDateTimeConverter localDateTimeConverter;

    private User testUser;
    private Coupon testCoupon;
    private IssuedCouponRequest request;

    @BeforeEach
    @Transactional
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("!Password1")
                .nickname("nickname")
                .address("address")
                .userRole(UserRole.USER)
                .build();

        userRepository.save(testUser);

        //수량을 10000개로 설정
        testCoupon = Coupon.builder()
                .name("Test Coupon")
                .description("Test Description")
                .discountAmount(1000)
                .quantity(10000)
                .startDate(localDateTimeConverter.toLocalDateTime("2025-03-24 00:00:00"))
                .endDate(localDateTimeConverter.toLocalDateTime("2025-03-28 23:59:59")).build();
        couponRepository.save(testCoupon);

        request = IssuedCouponRequest.builder()
                .couponId(testCoupon.getId())
                .build();
    }

    @Test
    void testReceiveCouponConcurrency() throws Exception {
        int numberOfThreads = 10;
        int testCount = 10000;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads, () -> {
            log.info("모든 스레드 준비 완료, 쿠폰 발급 시작");
        });
        CountDownLatch latch = new CountDownLatch(testCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger exceptionCount = new AtomicInteger(0);

        AuthUser authUser = AuthUser.builder()
                .id(testUser.getId())
                .userRole(UserRole.USER)
                .build();

        for (int i = 0; i < testCount; i++) {
            executorService.submit(() -> {
                try {
                    barrier.await();
                    userCouponService.issuedCoupon(authUser, request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptionCount.incrementAndGet();
                    ConcurrencyTest.log.info("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Coupon resultCoupon = couponRepository.findById(testCoupon.getId()).orElse(null);

        //재고를 10000개로 설정
        System.out.println("성공 카운트 : " + successCount);
        System.out.println("예외 카운트 : " + exceptionCount);
        System.out.println("실제 발급된 전체 쿠폰 개수 : " + userCouponRepository.count());
        System.out.println("쿠폰 테이블에서 예상한 발급된 쿠폰 개수 : " + (10000 - resultCoupon.getQuantity()));
    }
}