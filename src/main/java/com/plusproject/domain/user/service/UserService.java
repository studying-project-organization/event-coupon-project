package com.plusproject.domain.user.service;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.user.dto.request.UserChangePasswordRequest;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(Long userId, UserChangePasswordRequest userChangePasswordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_USER));

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.SAME_PASSWORD);
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        user.changePassword(passwordEncoder.encode(userChangePasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}
