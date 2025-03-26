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

***

## 동시성 이슈 해결

#### 핵심 목표 :

- 쿠폰 발급 시 동시 다발적 요청으로 인한 데이터 무결성 손상을 방지

- `Lettuce` 라이브러리를 이용해 Redis 락을 직접 구현 (`Redisson` 사용 금지)

기존과 동일하게 `k6`를 사용하여 부하 테스트를 통해서 동시성 제어 성능 테스트를 진행하였다.

```js
export const options = {
    stages: [
        { duration: '10s', target: 2000 },
        { duration: '80s', target: 2000 },
    ]
};
```

`k6` 옵션을 위와 같이 설정한 이유는 초기에 vus 수를 바로 220 초과해서 띄우면 부하가 걸려서 정상적으로 테스트가 되지 않아서 점점 늘려주는 방식을 선택했다.

- 테스트는 총 5번 정도 진행하였고 각각의 수치를 비교해보자
- `coupons` 테이블의 `quantity` 값은 항상 `50_000`을 고정으로 테스트하였다.
- 락을 획득하지 못했을때의 전략으로는 10초 동안 대기하면서 `100ms` 마다 `Retry` 하는 전략을 선택하였으며, 10초가 지나도 획득하지 못했을 경우엔 예외 처리를 해주었다.
- 2000명의 가상 유저를 처리하기 위해 `application.yml` 에서 `max-thread` 갯수를 500개로 늘려주었다.
- 테스트 시간 또한 `1분 30초`를 일정하게 유지하였다.

<details>
    <summary>첫번째 테스트</summary>
    
```
scenarios: (100.00%) 1 scenario, 2000 max VUs, 2m0s max duration (incl. graceful stop):
* default: Up to 2000 looping VUs for 1m30s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)

INFO[0023] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:17.5833493"}  source=console
INFO[0024] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:18.4310621"}  source=console
INFO[0027] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:21.8517795"}  source=console
INFO[0035] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:29.2747079"}  source=console
INFO[0035] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:29.826392"}  source=console
INFO[0050] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:44.7658323"}  source=console
INFO[0054] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:48.5265837"}  source=console
INFO[0055] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:49.9261035"}  source=console
INFO[0059] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:09:53.2547779"}  source=console
INFO[0070] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:10:04.9404414"}  source=console
INFO[0079] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:10:13.3951531"}  source=console

     ✗ status is 200
      ↳  99% — ✓ 29126 / ✗ 11

     █ setup

       ✓ login success

     checks.........................: 99.96% 29127 out of 29138
     data_received..................: 6.2 MB 64 kB/s
     data_sent......................: 11 MB  116 kB/s
     http_req_blocked...............: avg=19.65µs  min=0s     med=0s     max=2.11ms p(90)=0s      p(95)=0s
     http_req_connecting............: avg=16.74µs  min=0s     med=0s     max=2.11ms p(90)=0s      p(95)=0s
     http_req_duration..............: avg=5.04s    min=5.37ms med=5.35s  max=15.77s p(90)=5.52s   p(95)=5.58s
       { expected_response:true }...: avg=5.04s    min=5.37ms med=5.35s  max=14.79s p(90)=5.51s   p(95)=5.58s
     http_req_failed................: 0.03%  11 out of 29138
     http_req_receiving.............: avg=259.67µs min=0s     med=69.7µs max=2.65ms p(90)=792.6µs p(95)=919.2µs
     http_req_sending...............: avg=5.49µs   min=0s     med=0s     max=4.01ms p(90)=0s      p(95)=0s
     http_req_tls_handshaking.......: avg=0s       min=0s     med=0s     max=0s     p(90)=0s      p(95)=0s
     http_req_waiting...............: avg=5.04s    min=4.85ms med=5.35s  max=15.77s p(90)=5.51s   p(95)=5.58s
     http_reqs......................: 29138  300.788319/s
     iteration_duration.............: avg=6.04s    min=1s     med=6.35s  max=16.77s p(90)=6.52s   p(95)=6.59s
     iterations.....................: 29137  300.777996/s
     vus............................: 170    min=121            max=2000
     vus_max........................: 2000   min=2000           max=2000

                                                                                                                                                                                                                                    
running (1m36.9s), 0000/2000 VUs, 29137 complete and 0 interrupted iterations                                                                                                                                                       
default ✓ [======================================] 0000/2000 VUs  1m30s            
```

- 총 29126개의 요청 성공, 11개의 요청 실패
- 초당 약 300개의 요청을 처리함

#### 무결성 체크

![Image](https://github.com/user-attachments/assets/85787f27-59db-4d9d-af2d-8a8a352ea3fa)

- `50,000 - 29,126 = 20,874` 로 성공적으로 데이터가 처리되었다.   

</details>

<details>
    <summary>두번째 테스트</summary>

```
scenarios: (100.00%) 1 scenario, 2000 max VUs, 2m0s max duration (incl. graceful stop):
* default: Up to 2000 looping VUs for 1m30s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)

INFO[0053] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:16:25.7355326"}  source=console
INFO[0067] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:16:39.181462"}  source=console
INFO[0077] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:16:49.3820011"}  source=console
INFO[0078] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:16:50.0246855"}  source=console
INFO[0092] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:17:04.6009564"}  source=console

     ✗ status is 200
      ↳  99% — ✓ 31928 / ✗ 5

     █ setup

       ✓ login success

     checks.........................: 99.98% 31929 out of 31934
     data_received..................: 6.8 MB 70 kB/s
     data_sent......................: 12 MB  127 kB/s
     http_req_blocked...............: avg=4.36µs   min=0s     med=0s    max=15.85ms  p(90)=0s       p(95)=0s
     http_req_connecting............: avg=3.6µs    min=0s     med=0s    max=15.85ms  p(90)=0s       p(95)=0s
     http_req_duration..............: avg=4.5s     min=3.64ms med=4.76s max=15.34s   p(90)=5.01s    p(95)=5.23s
       { expected_response:true }...: avg=4.5s     min=3.64ms med=4.76s max=13.95s   p(90)=5.01s    p(95)=5.23s
     http_req_failed................: 0.01%  5 out of 31934
     http_req_receiving.............: avg=224.93µs min=0s     med=0s    max=9.1ms    p(90)=727.94µs p(95)=844.43µs
     http_req_sending...............: avg=1.06µs   min=0s     med=0s    max=540.69µs p(90)=0s       p(95)=0s
     http_req_tls_handshaking.......: avg=0s       min=0s     med=0s    max=0s       p(90)=0s       p(95)=0s
     http_req_waiting...............: avg=4.5s     min=2.72ms med=4.76s max=15.34s   p(90)=5.01s    p(95)=5.23s
     http_reqs......................: 31934  331.021512/s
     iteration_duration.............: avg=5.5s     min=1s     med=5.76s max=16.34s   p(90)=6.01s    p(95)=6.23s
     iterations.....................: 31933  331.011146/s
     vus............................: 40     min=40             max=2000
     vus_max........................: 2000   min=2000           max=2000

                                                                                                                                                                                                                                    
running (1m36.5s), 0000/2000 VUs, 31933 complete and 0 interrupted iterations                                                                                                                                                       
default ✓ [======================================] 0000/2000 VUs  1m30s  
```

- 총 31,928개의 요청 성공, 5개의 실패
- 초당 약 331개의 요청을 처리함

#### 무결성 체크

![Image](https://github.com/user-attachments/assets/328dbc58-e13f-4a89-9ced-36c1df470879)

- `50,000 - 31,928 = 18,072` 로 성공적으로 데이터가 처리되었다.

</details>

<details>
    <summary>세번째 테스트</summary>

```
scenarios: (100.00%) 1 scenario, 2000 max VUs, 2m0s max duration (incl. graceful stop):
* default: Up to 2000 looping VUs for 1m30s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)

INFO[0011] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:21:17.2576664"}  source=console
INFO[0011] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:21:17.2576664"}  source=console                                                
INFO[0049] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:21:55.4454327"}  source=console
INFO[0076] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:22:22.7218031"}  source=console

     ✗ status is 200
      ↳  99% — ✓ 31274 / ✗ 4

     █ setup

       ✓ login success

     checks.........................: 99.98% 31275 out of 31279
     data_received..................: 6.6 MB 69 kB/s
     data_sent......................: 12 MB  125 kB/s
     http_req_blocked...............: avg=5.06µs   min=0s     med=0s     max=14.03ms p(90)=0s      p(95)=0s
     http_req_connecting............: avg=4.29µs   min=0s     med=0s     max=14.03ms p(90)=0s      p(95)=0s
     http_req_duration..............: avg=4.62s    min=3.09ms med=4.91s  max=15.03s  p(90)=5.26s   p(95)=5.4s
       { expected_response:true }...: avg=4.61s    min=3.09ms med=4.91s  max=13.55s  p(90)=5.26s   p(95)=5.4s
     http_req_failed................: 0.01%  4 out of 31279
     http_req_receiving.............: avg=239.29µs min=0s     med=52.6µs max=13ms    p(90)=746.5µs p(95)=896.81µs
     http_req_sending...............: avg=1.79µs   min=0s     med=0s     max=13.53ms p(90)=0s      p(95)=0s
     http_req_tls_handshaking.......: avg=0s       min=0s     med=0s     max=0s      p(90)=0s      p(95)=0s
     http_req_waiting...............: avg=4.61s    min=3.09ms med=4.91s  max=15.03s  p(90)=5.26s   p(95)=5.4s
     http_reqs......................: 31279  324.807172/s
     iteration_duration.............: avg=5.62s    min=1s     med=5.91s  max=16.03s  p(90)=6.26s   p(95)=6.4s
     iterations.....................: 31278  324.796788/s
     vus............................: 65     min=65             max=2000
     vus_max........................: 2000   min=2000           max=2000

                                                                                                                                                                                                                                    
running (1m36.3s), 0000/2000 VUs, 31278 complete and 0 interrupted iterations                                                                                                                                                       
default ✓ [======================================] 0000/2000 VUs  1m30s      
```

- 총 31,274개의 요청 성공, 4개의 실패
- 초당 약 324개의 요청을 처리함

#### 무결성 체크

![Image](https://github.com/user-attachments/assets/e159aef1-4085-4f3d-bff4-ea64214c166b)

- `50,000 - 31,274 = 18,726` 로 성공적으로 데이터가 처리되었다.

</details>

<details>
    <summary>네번째 테스트</summary>

```
scenarios: (100.00%) 1 scenario, 2000 max VUs, 2m0s max duration (incl. graceful stop):
* default: Up to 2000 looping VUs for 1m30s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)

INFO[0013] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:25:43.6237933"}  source=console
INFO[0018] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:25:48.5059818"}  source=console
INFO[0036] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:26:06.4333486"}  source=console
INFO[0044] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:26:14.9042193"}  source=console
INFO[0054] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:26:24.346806"}  source=console
INFO[0074] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:26:44.4108496"}  source=console
INFO[0078] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:26:48.8190942"}  source=console
INFO[0089] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:26:59.545945"}  source=console

     ✗ status is 200
      ↳  99% — ✓ 29980 / ✗ 8

     █ setup

       ✓ login success

     checks.........................: 99.97% 29981 out of 29989
     data_received..................: 6.4 MB 66 kB/s
     data_sent......................: 12 MB  119 kB/s
     http_req_blocked...............: avg=15.74µs  min=0s     med=0s     max=5.81ms  p(90)=0s       p(95)=0s
     http_req_connecting............: avg=13.88µs  min=0s     med=0s     max=5.62ms  p(90)=0s       p(95)=0s
     http_req_duration..............: avg=4.87s    min=3.15ms med=5.11s  max=15.71s  p(90)=5.56s    p(95)=5.66s
       { expected_response:true }...: avg=4.87s    min=3.15ms med=5.11s  max=14.56s  p(90)=5.56s    p(95)=5.66s
     http_req_failed................: 0.02%  8 out of 29989
     http_req_receiving.............: avg=259.83µs min=0s     med=80.9µs max=10.03ms p(90)=789.94µs p(95)=915.1µs
     http_req_sending...............: avg=2.86µs   min=0s     med=0s     max=1ms     p(90)=0s       p(95)=0s
     http_req_tls_handshaking.......: avg=0s       min=0s     med=0s     max=0s      p(90)=0s       p(95)=0s
     http_req_waiting...............: avg=4.87s    min=3.15ms med=5.11s  max=15.71s  p(90)=5.56s    p(95)=5.66s
     http_reqs......................: 29989  309.29088/s
     iteration_duration.............: avg=5.87s    min=1s     med=6.11s  max=16.71s  p(90)=6.56s    p(95)=6.66s
     iterations.....................: 29988  309.280566/s
     vus............................: 1      min=1              max=2000
     vus_max........................: 2000   min=2000           max=2000

                                                                                                                                                                                                                                    
running (1m37.0s), 0000/2000 VUs, 29988 complete and 0 interrupted iterations                                                                                                                                                       
default ✓ [======================================] 0000/2000 VUs  1m30s         
```

- 총 29,980개의 요청 성공, 8개의 실패
- 초당 약 309개의 요청을 처리함

#### 무결성 체크

![Image](https://github.com/user-attachments/assets/1f125c15-4914-46b5-817e-479b83e779b7)

- `50,000 - 29,980 = 20,020` 로 성공적으로 데이터가 처리되었다.

</details>

<details>
    <summary>마지막 테스트</summary>

```
scenarios: (100.00%) 1 scenario, 2000 max VUs, 2m0s max duration (incl. graceful stop):
* default: Up to 2000 looping VUs for 1m30s over 2 stages (gracefulRampDown: 30s, gracefulStop: 30s)

INFO[0014] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:29:53.1434112"}  source=console
INFO[0020] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:29:58.9606357"}  source=console
INFO[0053] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:30:31.8802574"}  source=console
INFO[0053] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:30:32.7397963"}  source=console
INFO[0078] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락을 획득하지 못했습니다. 잠시 후 다시 시도해주세요.","timestamp":"2025-03-26T19:30:56.9282381"}  source=console

     ✗ status is 200
      ↳  99% — ✓ 30226 / ✗ 5

     █ setup

       ✓ login success

     checks.........................: 99.98% 30227 out of 30232
     data_received..................: 6.4 MB 66 kB/s
     data_sent......................: 12 MB  120 kB/s
     http_req_blocked...............: avg=7.02µs   min=0s     med=0s      max=1.5ms   p(90)=0s      p(95)=0s
     http_req_connecting............: avg=5.31µs   min=0s     med=0s      max=1.15ms  p(90)=0s      p(95)=0s
     http_req_duration..............: avg=4.83s    min=2.53ms med=5.3s    max=15.51s  p(90)=5.49s   p(95)=5.52s
       { expected_response:true }...: avg=4.83s    min=2.53ms med=5.3s    max=14.34s  p(90)=5.49s   p(95)=5.52s
     http_req_failed................: 0.01%  5 out of 30232
     http_req_receiving.............: avg=251.73µs min=0s     med=65.95µs max=1.81ms  p(90)=765.3µs p(95)=902.5µs
     http_req_sending...............: avg=2.63µs   min=0s     med=0s      max=651.8µs p(90)=0s      p(95)=0s
     http_req_tls_handshaking.......: avg=0s       min=0s     med=0s      max=0s      p(90)=0s      p(95)=0s
     http_req_waiting...............: avg=4.83s    min=2.53ms med=5.3s    max=15.51s  p(90)=5.49s   p(95)=5.52s
     http_reqs......................: 30232  311.204811/s
     iteration_duration.............: avg=5.83s    min=1s     med=6.3s    max=16.52s  p(90)=6.49s   p(95)=6.52s
     iterations.....................: 30231  311.194518/s
     vus............................: 3      min=3              max=2000
     vus_max........................: 2000   min=2000           max=2000

                                                                                                                                                                                                                                    
running (1m37.1s), 0000/2000 VUs, 30231 complete and 0 interrupted iterations                                                                                                                                                       
default ✓ [======================================] 0000/2000 VUs  1m30s        
```

- 총 30,226개의 요청 성공, 5개의 실패
- 초당 약 311개의 요청을 처리함

#### 무결성 체크

![Image](https://github.com/user-attachments/assets/739bb6b8-cc99-4785-946d-1494f2d41d8d)

- `50,000 - 30,226 = 19,774` 로 성공적으로 데이터가 처리되었다.

</details>

### 결과 합산

- 최대 요청 성공 수 : 31,928개
- 최대 요청 실패 수 : 11개

- 총 요청 성공 수 : 152,534개
- 총 요청 실패 수 : 33개

- 평균 요청 성공 수 : 30,506.8개
- 평균 요청 실패 수 : 6.6개

<br/>

#### 성공 비율

- 요청 성공률 : 약 `99.98%`
- 요청 실패율 : 약 `0.02%`
- 데이터 무결성 : `100%`

매우 준수한 결과를 나타낸것 같다.
목표로 하였던 데이터 무결성을 지켜내었기에 만족스러운 결과이다.
