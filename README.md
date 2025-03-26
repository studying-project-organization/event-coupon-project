## 동시성 문제 테스트

- k6 를 사용하여 부하 테스트를 진행하여 실제 DB에 접근할때 동시성 관련 기능을 사용하지 않으면 발생하는 문제 파악


- 초기 Coupon 테이블에 `Quantity` 를 30,000 개로 설정하여서 테스트 진행
- k6 파일 내부 설정의 가상유저는 1,000명 부하 테스트는 30초 동안 진행

### 실행 결과
```
     ✗ status is 200
      ↳  26% — ✓ 7836 / ✗ 22164

     █ setup

       ✓ login success

     checks.........................: 26.12% 7837 out of 30001
     data_received..................: 7.1 MB 229 kB/s
     data_sent......................: 11 MB  369 kB/s
     http_req_blocked...............: avg=1.32ms   min=0s     med=0s     max=118.68ms p(90)=0s      p(95)=0s
     http_req_connecting............: avg=1.3ms    min=0s     med=0s     max=117.68ms p(90)=0s      p(95)=0s
     http_req_duration..............: avg=15.56ms  min=0s     med=3.75ms max=718.61ms p(90)=8.2ms   p(95)=17.8ms
       { expected_response:true }...: avg=12.73ms  min=1.03ms med=3.63ms max=658.33ms p(90)=6.94ms  p(95)=11.86ms
     http_req_failed................: 73.87% 22164 out of 30001
     http_req_receiving.............: avg=105.25µs min=0s     med=0s     max=21.88ms  p(90)=504.4µs p(95)=594µs
     http_req_sending...............: avg=203.33µs min=0s     med=0s     max=32.35ms  p(90)=0s      p(95)=0s
     http_req_tls_handshaking.......: avg=0s       min=0s     med=0s     max=0s       p(90)=0s      p(95)=0s
     http_req_waiting...............: avg=15.25ms  min=0s     med=3.68ms max=718.61ms p(90)=8.02ms  p(95)=17.46ms
     http_reqs......................: 30001  969.789322/s
     iteration_duration.............: avg=1.01s    min=1s     med=1s     max=1.75s    p(90)=1s      p(95)=1.03s
     iterations.....................: 30000  969.756997/s
     vus............................: 1000   min=1000           max=1000
     vus_max........................: 1000   min=1000           max=1000


running (0m30.9s), 0000/1000 VUs, 30000 complete and 0 interrupted iterations
default ✓ [======================================] 1000 VUs  30s
```

- 총 7836 개의 성공, 22164개의 실패가 나왔다. 실제 DB에는 어떻게 적용되었는지 확인해보자.

먼저 User와 Coupon 은 `다대다` 관계로 중간 관리 테이블을 만들어 주었고 쿠폰 발급시 해당 테이블에 Row 가 1개씩 생성된다.

### MySQL 콘솔
```mysql
use plus_project;

select count(*) from user_coupon;
```

해당 SQL 문을 실행한 결과의 이미지이다.

![Image](https://github.com/user-attachments/assets/a759252a-9c62-427c-9ae3-a1162f982e64)

정상적으로 7836 개가 생성된 것을 확인할 수 있었다.

그럼 실제 `Coupons` 테이블의 `Quantity` 컬럼에도 잘 적용되었는지 이미지로 확인해보자

![Image](https://github.com/user-attachments/assets/780fa16b-d6bf-4d00-8b21-b766fc85b25c)

`30000 - 7836 = 22,164` 개가 됐어야 했는데 `25,931`개로 동시성 문제가 확실하게 생겼음을 인지할 수 있었다.

![Image](https://github.com/user-attachments/assets/b4f0aac8-25a9-4724-9e6c-e03bbb9f4533)

심지어 위의 이미지와 같이 k6 마지막에 오류 발생시 로그를 찍어보았을때 데이터베이스 데드락이 발생하여 제외된 부분 22164 개를 제외하고도,
무려 `25,931 - 22,164 = 3,767`, 즉 `3,767`개의 잘못된 데이터가 생성되고 있음을 확인할 수 있었다.

이제 동시성 문제를 해결해보자.