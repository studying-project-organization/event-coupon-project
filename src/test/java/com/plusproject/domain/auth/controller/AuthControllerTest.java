package com.plusproject.domain.auth.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.auth.dto.request.SigninRequest;
import com.plusproject.domain.auth.dto.request.SignupRequest;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private HttpHeaders headers;

    @Nested
    class 회원가입_로그인_테스트 {
        @BeforeEach
        public void setUp() {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            userRepository.deleteAll();
        }

        @Test
        void 회원가입_시도_성공() {
            // Given
            SignupRequest request = new SignupRequest("test1@example.com", "!Password1", "nickname1", "address");
            HttpEntity<SignupRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/auth/signup",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            String responseBody = response.getBody();
            assertTrue(responseBody.contains("회원 가입에 성공했습니다."));
            assertTrue(responseBody.contains("bearerToken"));

            // DB에 저장된 사용자 확인
            User savedUser = userRepository.findByEmail("test1@example.com").orElse(null);
            assertNotNull(savedUser);
            assertEquals("nickname1", savedUser.getNickname());
            assertTrue(passwordEncoder.matches("!Password1", savedUser.getPassword()));
        }

        @Test
        public void 중복된_이메일로_회원가입_시도_시_실패() {
            // Given: 이미 존재하는 사용자
            User existingUser = User.toEntity("test1@example.com", passwordEncoder.encode("!Password1"), "nickname1",
                    "address", UserRole.USER);
            userRepository.save(existingUser);

            SignupRequest request = new SignupRequest("test1@example.com", "!Password1234", "nickname2", "address2");
            HttpEntity<SignupRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/auth/signup",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().contains(ErrorCode.BAD_REQUEST_EXIST_EMAIL.getMessage()));
        }

        @Test
        public void 로그인_시도_성공() {
            // Given: 가입된 사용자
            User user = User.toEntity("test1@example.com", passwordEncoder.encode("!Password1"), "nickname1", "address",
                    UserRole.USER);
            userRepository.save(user);

            SigninRequest request = new SigninRequest("test1@example.com", "!Password1");
            HttpEntity<SigninRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/auth/signin",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            String responseBody = response.getBody();
            assertTrue(responseBody.contains("로그인에 성공했습니다."));
            assertTrue(responseBody.contains("bearerToken"));
        }

        @Test
        public void 틀린_비밀번호로_로그인_시도_시_실패() {
            // Given: 가입된 사용자
            User user = User.toEntity("test1@example.com", passwordEncoder.encode("!Password1"), "nickname1", "address",
                    UserRole.USER);
            userRepository.save(user);

            SigninRequest request = new SigninRequest("test1@example.com", "!Password1234");
            HttpEntity<SigninRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/auth/signin",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertTrue(response.getBody().contains(ErrorCode.UNAUTHORIZED_WRONG_PASSWORD.getMessage()));
        }

        @Test
        public void 존재하지_않는_사용자로_로그인_시도_시_실패() {
            // Given: 존재하지 않는 사용자
            SigninRequest request = new SigninRequest("test1@example.com", "!Password1");
            HttpEntity<SigninRequest> entity = new HttpEntity<>(request, headers);

            // When
            ResponseEntity<String> response = restTemplate.exchange(
                    "/api/v1/auth/signin",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // Then
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertTrue(response.getBody().contains(ErrorCode.BAD_REQUEST_NOT_SIGN_UP_USER.getMessage()));
        }
    }
}