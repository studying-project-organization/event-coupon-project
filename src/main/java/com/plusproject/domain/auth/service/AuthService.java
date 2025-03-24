package com.plusproject.domain.auth.service;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.config.JwtUtil;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.auth.dto.request.SigninRequest;
import com.plusproject.domain.auth.dto.request.SignupRequest;
import com.plusproject.domain.auth.dto.response.SigninResponse;
import com.plusproject.domain.auth.dto.response.SignupResponse;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new ApplicationException(ErrorCode.BAD_REQUEST_EXIST_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User newUser = User.toEntity(
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getNickname(),
                requestDto.getAddress(),
                UserRole.USER
        );

        User savedUser = userRepository.save(newUser);
        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getUserRole());
        return SignupResponse.of(bearerToken);
    }

    @Transactional(readOnly = true)
    public SigninResponse signin(SigninRequest requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(
                () -> new ApplicationException(ErrorCode.BAD_REQUEST_NOT_SIGN_UP_USER)
        );

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED_WRONG_PASSWORD);
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getUserRole());
        return SigninResponse.of(bearerToken);
    }
}
