# 동시성 테스트

- k6로 동시성 테스트 진행
- Coupon테이블에 쿠폰 Quantity 50000개를 미리 설정
  
  ```
    export const options = {
        stages: [
            { duration: '10s', target: 2000 },
            { duration: '80s', target: 2000 },
        ]
    };
  ```
- 가상의 유저 2000명을 기준으로 점진적 과부화를 줌으로서 테스트 실행
- 90초동안 지속적으로 쿠폰을 발급 받을 때의 동시성 테스트 및 서버 부하 상태 확인

## 실행 결과 
- Lettuce Lock과 redisson Lock 두 번을 나누어 테스트 진행
