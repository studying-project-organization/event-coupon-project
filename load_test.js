import http from 'k6/http';
import {sleep, check} from 'k6';

// 테스트 옵션 설정
export const options = {
/*    vus: 200, // 가상 유저 수
    duration: '20s', // 테스트 지속 시간*/

    stages: [
        { duration: '10s', target: 2000 },
        { duration: '40s', target: 2000 },
    ]
};

// JWT 토큰
let token;

// 초기 데이터 설정
export function setup() {
    const loginSetting = JSON.stringify({
        email: 'test1@example.com',
        password: '!Password1'
    });
    const loginHeaders = { 'Content-Type': 'application/json' };
    const loginRes = http.post('http://localhost:8080/api/v1/auth/signin', loginSetting, { headers: loginHeaders });
    check(loginRes, { 'login success': (r) => r.status === 200 });
    token = loginRes.json('data.accessToken');
    return { token };
}

export default function (data) {
    // 각 VU마다 고유한 요청 ID와 타임스탬프 생성
/*    const requestId = `${__VU}-${__ITER}`; // VU 번호와 반복 횟수 조합
    const timestamp = new Date().toISOString(); // 요청 시점 기록*/

    // 요청 페이로드
    const payload = JSON.stringify({
        couponId: 1, // 테스트용 couponId
/*        requestId: requestId, // 서버로 전달할 요청 ID
        timestamp: timestamp, // 요청 시점*/
    });
    
    // Autorization 위한 Token headers에 삽입
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `${data.token}`,
    };

    // API 호출
    const res = http.post(`http://localhost:8080/api/v1/user-coupon`, payload, { headers });
    // 응답 검증
    check(res, {
        'status is 200': (r) => r.status === 200, // 성공 시 200 반환
    });
    sleep(1);

    if (res.status !== 200) {
        console.log(`Failed: ${res.status} - ${res.body}`);
    }

    /*console.log(`Request ID: ${requestId}, Timestamp: ${timestamp}, Response: ${res.body}`);*/
}