# 동시성 테스트

- k6로 동시성 테스트 진행
- Coupon테이블에 쿠폰 Quantity 50000개를 미리 설정
- 

```
  export const options = {
      stages: [
          { duration: '10s', target: 2000 },
          { duration: '80s', target: 2000 },
      ]
  };
```
- 가상의 유저 2000명을 기준으로 점진적 과부화를 줌으로서 테스트 실행
