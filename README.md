# 동시성 테스트

- k6로 동시성 테스트 진행  
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
  
### Lettuce Lock 실행
```
       ✓ status is 200

     █ setup

       ✓ login success

     checks.........................: 100.00% 8145 out of 8145
     data_received..................: 1.7 MB  15 kB/s
     data_sent......................: 3.1 MB  27 kB/s
     http_req_blocked...............: avg=292.54µs min=0s    med=0s     max=38.61ms p(90)=1.05ms   p(95)=1.24ms
     http_req_connecting............: avg=261.37µs min=0s    med=0s     max=38.61ms p(90)=1.04ms   p(95)=1.11ms
     http_req_duration..............: avg=22.98s   min=1.63s med=24.54s max=41.14s  p(90)=32.16s   p(95)=32.46s
       { expected_response:true }...: avg=22.98s   min=1.63s med=24.54s max=41.14s  p(90)=32.16s   p(95)=32.46s
     http_req_failed................: 0.00%   0 out of 8145
     http_req_receiving.............: avg=247.59µs min=0s    med=0s     max=13.9ms  p(90)=595.38µs p(95)=836.75µs
     http_req_sending...............: avg=57.33µs  min=0s    med=0s     max=5.06ms  p(90)=0s       p(95)=531.47µs
     http_req_tls_handshaking.......: avg=0s       min=0s    med=0s     max=0s      p(90)=0s       p(95)=0s
     http_req_waiting...............: avg=22.98s   min=1.63s med=24.54s max=41.14s  p(90)=32.16s   p(95)=32.46s
     http_reqs......................: 8145    68.763521/s
     iteration_duration.............: avg=23.98s   min=2.63s med=25.54s max=42.14s  p(90)=33.16s   p(95)=33.46s
     iterations.....................: 8144    68.755079/s
     vus............................: 29      min=0            max=2000
     vus_max........................: 2000    min=2000         max=2000


running (1m58.4s), 0000/2000 VUs, 8144 complete and 0 interrupted iterations
default ✓ [======================================] 0000/2000 VUs  1m30s
```
