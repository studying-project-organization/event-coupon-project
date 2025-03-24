package com.plusproject.domain.user.service;

import com.plusproject.common.dto.AuthUser;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.user.dto.request.UpdateAdminRequest;
import com.plusproject.domain.user.dto.request.UpdatePasswordRequest;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateUserRole(AuthUser authUser, Long userId, @Valid UpdateAdminRequest request) {
        if (authUser.getId().equals(userId)) {
            throw new ApplicationException(ErrorCode.INVALID_SELF_ROLE_UPDATE);
        }

        User findUser = userRepository.findByIdOrElseThrow(userId, ErrorCode.NOT_FOUND_USER);

        if (findUser.getUserRole().equals(UserRole.ADMIN)) {
            throw new ApplicationException(ErrorCode.INVALID_ADMIN_ROLE_UPDATE);
        }

        findUser.updateUserRole(request.getUserRole());
    }

    @Transactional
    public void updatePassword(AuthUser authUser, UpdatePasswordRequest request) {
        User findUser = userRepository.findByIdOrElseThrow(authUser.getId(), ErrorCode.NOT_FOUND_USER);

        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new ApplicationException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        if (!passwordEncoder.matches(request.getOldPassword(), findUser.getPassword())) {
            throw new ApplicationException(ErrorCode.INCORRECT_PASSWORD);
        }

        findUser.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }
}
