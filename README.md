
## 동시성 이슈를 검증할 수 있는 테스트

### 1. local test 결과
[ConcurrencyTest](src/test/java/com/plusproject/load/ConcurrencyTest.java)에서 실행

#### 설정
    
- 수량 - 10000개
- 쓰레드 - 10개
- 테스트 횟수 - 1000번
- 결과
```
성공 카운트 : 1273
예외 카운트 : 8727
실제 발급된 전체 쿠폰 개수 : 1273
쿠폰 테이블에서 예상한 발급된 쿠폰 개수 : 1001
```
결과를 보면 총 성공 개수와 실제 발급된 쿠폰 개수는 1273개로 동일하다.
하지만 실제 쿠폰 테이블에서 발급된 수량은 1001개로 약 250개의 차이를 보인다.
또한, 쿠폰 테이블의 수량을 업데이트하기위한 요청이 동시에 들어오면서, 데드락이 발생하는 문제도 발견되었다.
본인의 예상으로는 예외 카운트 중 대부분이 데드락으로 인한 에러일 가능성이 많다고 생각한다.

### 2. k6 테스트 결과
테스트코드를 직접 구현해보는것과 더불어,
k6 부하테스트 툴을 사용하여 실제 Application이 동작하였을때의 테스트도 진행해 보았다.
[load_test](load_test.js)를 참고하면 된다.

```
  █ TOTAL RESULTS

    checks_total.......................: 6099   256.837294/s
    checks_succeeded...................: 48.61% 2965 out of 6099
    checks_failed......................: 51.38% 3134 out of 6099

    ✓ login success
    ✗ status is 200
      ↳  48% — ✓ 2964 / ✗ 3134

    HTTP
    http_req_duration.......................................................: avg=14.43ms min=0s     med=6ms    max=427.35ms p(90)=12.2ms  p(95)=15.92ms
      { expected_response:true }............................................: avg=9.46ms  min=3.44ms med=4.83ms max=424.88ms p(90)=10.61ms p(95)=13ms
    http_req_failed.........................................................: 51.38% 3134 out of 6099
    http_reqs...............................................................: 6099   256.837294/s

    EXECUTION
    iteration_duration......................................................: avg=1.72s   min=1s     med=1.28s  max=7.02s    p(90)=2.52s   p(95)=5.49s
    iterations..............................................................: 6098   256.795182/s
    vus.....................................................................: 37     min=37           max=500
    vus_max.................................................................: 500    min=500          max=500

    NETWORK
    data_received...........................................................: 1.5 MB 63 kB/s
    data_sent...............................................................: 2.0 MB 83 kB/s

```

총 6099개 중 2964개가 성공하였다고 나와있다.

발급된 쿠폰의 개수를 
```sql
SELECT COUNT(*) FROM user_coupon
```
를 통해 확인해보면, 

![img.png](img/발급된%20쿠폰의%20개수.png)

처럼 2964개가 되어있다.

하지만 쿠폰 테이블에서 해당 쿠폰의 수량을 확인해보면

![img.png](img/k6를%20이용한%20동시성%20테스트.png)

과 같이 7955개로 나와있다. (초기값은 10000개로 설정하였다.)
10000 - 7955 = 2045이므로, 해당 쿠폰은 2045개만 발급되었다고 생각하지만, 실제로는 2964개로, 약 900개가 더 발급 된 것을 확인할 수 있다.
이를 통해 동시성 테스트 시 문제가 발생할 수 있다는 것을 알 수 있다.

## Redis Lettuce

### Lettuce 구현
[RedisLettuceService](src/main/java/com/plusproject/domain/redisLettuce/service/RedisLettuceService.java), [RedisLettuceRepository](src/main/java/com/plusproject/domain/redisLettuce/repository/RedisLettuceRepository.java)

- 먼저 레디스를 도커를 통해 실행시켜주었다.
- acquireLock -> lock 획득을 시도하는 메서드
  - 만약 정한 시간 안에 lock 획득을 성공하면, redis에 해당 lock을 저장하고, 획득 성공을 반환한다.
  - 만약 실패하면, null을 반환해 실패했다는 것을 알려준다.
- releaseLock -> lock을 해제하는 메서드
  - lock을 얻은 후 실행이 완료되면 lock을 해제해야 한다.
  - 해당 메서드를 통해 redis 메모리에서 해당 값을 찾아 제거하면 lock이 해제하는 것.
- executeWithLock -> lock을 통해 실행하는 메서드
  - 먼저 redis 메모리에 저장하기위해 lock:coupon:아이디 값을 String으로 생성한다.
  - 해당 값을 사용해 lock을 획득할 수 있는 지 체크하고, 획득 하였다면, value를 return 받는다.
  - 실패한다면 value = null이다.
  - 만약 value가 null이면 lock 획득에 실패했다는 예외처리를 해준다.
  - lock을 획득했다면, task를 실행하고, 마지막에 lock을 해제 해준다.

### UserCouponService 수정
[UserCouponService](src/main/java/com/plusproject/domain/usercoupon/service/UserCouponService.java)

- 서비스에서 쿠폰을 발급받는 메서드인 issuedCoupon 메서드를 수정해준다.
- redisLettuceService를 통해 lock 획득 이후 실행할 수 있게 끔 코드를 수정해 주었다.
- 추가로, 해당 메서드는 발급받은 쿠폰의 id를 반환해주어야 하기 때문에, 해당 값을 반환해준다.


### 결과
```
INFO[0011] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락 획득에 실패했습니다.","timestamp":"2025-03-27T10:31:48.4537876"}  source=console
INFO[0013] Failed: 409 - {"status":"CONFLICT","code":409,"message":"락 획득에 실패했습니다.","timestamp":"2025-03-27T10:31:50.2757519"}  source=console


  █ TOTAL RESULTS

    checks_total.......................: 7955   150.081395/s
    checks_succeeded...................: 99.97% 7953 out of 7955
    checks_failed......................: 0.02%  2 out of 7955

    ✓ login success
    ✗ status is 200
      ↳  99% — ✓ 7952 / ✗ 2

    HTTP
    http_req_duration.......................................................: avg=9.36s  min=316.01ms med=11.43s max=15.79s p(90)=11.73s p(95)=11.74s
      { expected_response:true }............................................: avg=9.36s  min=316.01ms med=11.43s max=15.79s p(90)=11.73s p(95)=11.74s
    http_req_failed.........................................................: 0.02%  2 out of 7955
    http_reqs...............................................................: 7955   150.081395/s

    EXECUTION
    iteration_duration......................................................: avg=10.36s min=1.4s     med=12.43s max=16.79s p(90)=12.73s p(95)=12.74s
    iterations..............................................................: 7954   150.062529/s
    vus.....................................................................: 2      min=2         max=2000
    vus_max.................................................................: 2000   min=2000      max=2000

    NETWORK
    data_received...........................................................: 1.7 MB 32 kB/s
    data_sent...............................................................: 2.7 MB 52 kB/s
```
k6 load_test를 돌린 결과이다.
두번의 락 획득 실패로인한 실패 이외에는 총 7952개의 쿠폰 발급이 이루어졌다.
발급 된 쿠폰의 개수를
```sql
SELECT COUNT(*) FROM user_coupon
```
통해 확인해보면,

![img.png](img/lettuce%20사용%20후%20k6%20load_test%20결과.png)

다음과 같이 7952개의 쿠폰 발급이 정상적으로 이루어진것을 알 수 있다.

쿠폰 수량의 변화를 확인해보면,

![img_1.png](img/lettuce%20사용%20후%20coupon%20결과.png)

다음과 같이 2048개가 남아있다.(설정 수량은 10000개 이다.)
10000 - 2048 = 7952이므로, 동시성 처리가 제대로 된 것을 확인할 수 있다.


## Trouble Shooting

### 1. k6 테스트 시 연결이 되지 않는다는 에러 발생.

#### 발단

```
WARN[0046] Request Failed                                error="Post \"http://localhost:8080/api/v1/user-coupon\": dial tcp 127.0.0.1:8080: connectex: No connection could be made because the target machine actively refused it."
```
다음과 같은 에러 발생.
해당 에러를 확인해보니, 

```js
vus: 200, // 가상 유저 수
duration: '20s', // 테스트 지속 시간
```
가상 유저수를 200으로 잡고 시작하였을 때, 요청이 동시에 많이 들어가 서버에서 해당 연결을 거부한 것으로 추정하였다.

#### 해결

따라서, 
```js
stages: [
        { duration: '10s', target: 2000 },
        { duration: '30s', target: 2000 },
    ]
```
다음과 같이 단계적으로 가상 유저수를 늘리는 설정으로 테스트를 해보았더니, 정상작동 하였다.

### 2. lettuce 구현 이후 k6 테스트 시 동시성 처리 문제가 계속 발생

#### 발단

Redis Lettuce 구현 이후 k6 테스트 시 동시성 처리가 되어야 함에도 불구하고, 동시성 처리가 제대로 이루어지지 않음.
ex) 발급은 8500개가 되었는데, 쿠폰 수량은 7000개가 줄어있는 경우

#### 해결
기존 UserCouponService에서는 해당 쿠폰의 수량을 엔티티를 통해 줄이고, 엔티티 매니저에서 알아서 처리하도록 구현하였음.
해당 방식을 couponRepository에서 바로 수량을 줄일 수 있도록 수정하였더니, 정상적으로 처리 됨.