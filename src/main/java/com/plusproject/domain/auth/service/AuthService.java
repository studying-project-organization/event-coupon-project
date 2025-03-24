package com.plusproject.domain.auth.service;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(@Valid SignupRequest request) {

        User newUser = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .nickname(request.getNickname())
            .userRole(UserRole.USER)
            .address(request.getAddress())
            .build();

        User savedUser = userRepository.save(newUser);

        return savedUser.getId();
    }

    @Transactional
    public AccessTokenResponse signin(SigninRequest request) {
        User findUser = userRepository.findUserByEmailOrElseThrow(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), findUser.getPassword())) {
            throw new ApplicationException(ErrorCode.INCORRECT_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(findUser.getId(), findUser.getUserRole());
        return AccessTokenResponse.of(accessToken);
    }

}
