package com.plusproject.domain.user.service;

import com.plusproject.SpringBootTestSupport;
import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.user.dto.request.UpdateAdminRequest;
import com.plusproject.domain.user.dto.request.UpdatePasswordRequest;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
class UserServiceTest extends SpringBootTestSupport {

    private static final String NOT_FOUND_VALUE = " id = -1";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager em;

    private User user;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .email("test@example.com")
            .password(passwordEncoder.encode("Password1234!"))
            .nickname("테스트")
            .address("서울")
            .userRole(UserRole.ADMIN)
            .build();

        userRepository.save(user);

        em.flush();
        em.clear();

        authUser = AuthUser.builder()
            .id(user.getId())
            .userRole(user.getUserRole())
            .build();
    }

    @Test
    @DisplayName("유저 권한 변경 - 성공")
    void updateUserRole1() throws Exception {
        // given
        User testUser = createTestUser(UserRole.USER);

        User savedTestUser = userRepository.save(testUser);

        UpdateAdminRequest request = UpdateAdminRequest.builder()
            .userRole(UserRole.ADMIN)
            .build();

        // when
        userService.updateUserRole(authUser, savedTestUser.getId(), request);

        // then
        assertThat(savedTestUser.getUserRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    @DisplayName("유저 권한 변경시 자기 자신의 권한을 변경하려하면 INVALID_SELF_ROLE_UPDATE 예외 발생")
    void updateUserRole2() throws Exception {
        // given
        UpdateAdminRequest request = UpdateAdminRequest.builder()
            .userRole(UserRole.USER)
            .build();

        // when & then
        assertThatThrownBy(() -> userService.updateUserRole(authUser, user.getId(), request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.INVALID_SELF_ROLE_UPDATE.getMessage());
    }

    @Test
    @DisplayName("유저 권한 변경시 권한 변경할 대상 유저를 찾지 못하면 NOT_FOUND_USER 예외 발생")
    void updateUserRole3() throws Exception {
        // given
        UpdateAdminRequest request = UpdateAdminRequest.builder()
            .userRole(UserRole.USER)
            .build();

        // when & then
        assertThatThrownBy(() -> userService.updateUserRole(authUser, -1L, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage() + NOT_FOUND_VALUE);
    }

    @Test
    @DisplayName("유저 권한 변경시 권한 변경할 대상 유저가 ADMIN 이면 INVALID_ADMIN_ROLE_UPDATE  예외 발생")
    void updateUserRole4() throws Exception {
        // given
        User testUser = createTestUser(UserRole.ADMIN);

        User savedTestUser = userRepository.save(testUser);

        UpdateAdminRequest request = UpdateAdminRequest.builder()
            .userRole(UserRole.USER)
            .build();

        // when & then
        assertThatThrownBy(() -> userService.updateUserRole(authUser, savedTestUser.getId(), request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.INVALID_ADMIN_ROLE_UPDATE.getMessage());
    }

    @Test
    @DisplayName("유저 비밀번호 변경 - 성공")
    void updatePassword1() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        // when
        userService.updatePassword(authUser, request);
        User findUser = userRepository.findByIdOrElseThrow(authUser.getId(), ErrorCode.NOT_FOUND_USER);

        // then
        assertThat(passwordEncoder.matches(request.getNewPassword(), findUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("유저 비밀번호 변경시 대상 유저를 찾지 못하면 NOT_FOUND_USER 예외 발생")
    void updatePassword2() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password12345!")
            .build();

        AuthUser testAuthUser = AuthUser.builder()
            .id(-1L)
            .userRole(UserRole.ADMIN)
            .build();

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(testAuthUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.NOT_FOUND_USER.getMessage() + NOT_FOUND_VALUE);
    }

    @Test
    @DisplayName("유저 비밀번호 변경시 기존 비밀번호와 새 비밀번호가 같다면 SAME_AS_OLD_PASSWORD 예외 발생")
    void updatePassword3() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("Password1234!")
            .newPassword("Password1234!")
            .build();

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.SAME_AS_OLD_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("유저 비밀번호 변경시 기존 비밀번호가 일치하지 않다면 INCORRECT_PASSWORD 예외 발생")
    void updatePassword4() throws Exception {
        // given
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
            .oldPassword("WrongPassword!!")
            .newPassword("Password12345!")
            .build();

        // when & then
        assertThatThrownBy(() -> userService.updatePassword(authUser, request))
            .isInstanceOf(ApplicationException.class)
            .hasMessage(ErrorCode.INCORRECT_PASSWORD.getMessage());
    }


    private User createTestUser(UserRole userRole) {
        return User.builder()
            .email("test2@example.com")
            .password("Password1234!")
            .nickname("홍길동")
            .address("서울")
            .userRole(userRole)
            .build();
    }
}