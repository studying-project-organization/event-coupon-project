package com.plusproject.domain.auth.service;

import com.plusproject.SpringBootTestSupport;
import com.plusproject.common.auth.JwtUtil;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.auth.dto.request.SigninRequest;
import com.plusproject.domain.auth.dto.request.SignupRequest;
import com.plusproject.domain.auth.dto.response.AccessTokenResponse;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class AuthServiceTest extends SpringBootTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private EntityManager em;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("test@example.com")
            .password(passwordEncoder.encode("Password1234!"))
            .nickname("테스트")
            .address("서울")
            .userRole(UserRole.USER)
            .build();

        userRepository.save(user);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void signup1() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
            .email("abc@abc.com")
            .password("Password1234!")
            .nickname("테스트")
            .address("서울")
            .build();


        // when
        Long userId = authService.signup(request);
        User findUser = userRepository.findByIdOrElseThrow(userId, ErrorCode.NOT_FOUND_USER);

        // then
        assertThat(findUser)
            .extracting("id", "email", "nickname", "address")
            .containsExactly(
                userId,
                request.getEmail(),
                request.getNickname(),
                request.getAddress()
            );
    }

    @Test
    @DisplayName("로그인 - 성공")
    void signin1() throws Exception {
        // given
        SigninRequest request = SigninRequest.builder()
            .email("test@example.com")
            .password("Password1234!")
            .build();

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getUserRole());

        // when
        AccessTokenResponse response = authService.signin(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    @DisplayName("로그인 시도할 때 이메일에 해당하는 유저를 찾지 못해 NOT_FOUND_USER 예외가 발생한다.")
    void signin2() throws Exception {
        // given
        SigninRequest request = SigninRequest.builder()
            .email("wrong@example.com")
            .password("Password1234!")
            .build();

        // when & then
        assertThatThrownBy(() -> authService.signin(request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage());
    }

    @Test
    @DisplayName("로그인 시도할 때 비밀번호가 틀리면 INCORRECT_PASSWORD 예외가 발생한다.")
    void signin3() throws Exception {
        // given
        SigninRequest request = SigninRequest.builder()
            .email("test@example.com")
            .password("WrongPassword!!")
            .build();

        // when & then
        assertThatThrownBy(() -> authService.signin(request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.INCORRECT_PASSWORD.getMessage());
    }
}