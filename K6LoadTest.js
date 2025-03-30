import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
    stages: [
        { duration: '10s', target: 2000 },
        { duration: '80s', target: 2000 },
    ]
};

const redissonUrl = 'http://localhost:8080/api/v1/user-coupon';
const mysqlUrl = "http://localhost:8080/api/v1/user-coupon/mysql";
let token;

export function setup() {
    const loginPayload = JSON.stringify({
        email: 'hong@naver.com',
        password: 'Password1234!'
    });
    const loginHeaders = { 'Content-Type': 'application/json' };
    const loginRes = http.post('http://localhost:8080/api/v1/auth/signin', loginPayload, { headers: loginHeaders });
    check(loginRes, { 'login success': (r) => r.status === 200 });
    token = loginRes.json('data.accessToken');
    return { token };
}

export default function (data) {
    const payload = JSON.stringify({ couponId: 1 });
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `${data.token}`,
    };

    const res = http.post(redissonUrl, payload, { headers });
    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(1);

    if (res.status !== 200) {
        console.log(`Failed: ${res.status} - ${res.body}`);
    }
}