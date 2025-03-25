package com.plusproject.domain.user.service;

import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.common.jwt.JwtUtil;
import com.plusproject.config.PasswordEncoder;
import com.plusproject.domain.user.dto.request.CreateUserRequest;
import com.plusproject.domain.user.dto.request.LoginRequest;
import com.plusproject.domain.user.entity.Users;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public ApiResponse<Void> createUser(CreateUserRequest req) {
        Users users = new Users(req);

        if (!userRepo.existsByEmail(req.getEmail())) {
            userRepo.save(users);
            return ApiResponse.success(req.getNickname() + " 회원가입 성공");
        } else {
            throw new ApplicationException(ErrorCode.DUPLICATED_EMAIL, "중복된 이메일입니다.");
        }
    }

    public Users findUser(Long id) {
        return userRepo.findByIdOrElseThrow(id, ErrorCode.NOT_FOUND_USER);
    }

    public ApiResponse<String> login(LoginRequest req){
        Users user = userRepo.findByEmail(req.getEmail()).orElseThrow(() ->
                new ApplicationException(ErrorCode.NOT_FOUND_USER, "이메일을 다시 입력해주세요."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.NOT_FOUND_USER, "비밀번호를 다시 입력해주세요.");
        }

        String token = jwtUtil.generateToken(user.getId()).get("token");

        return ApiResponse.success(token, "로그인 성공");
    }

    public ApiResponse<Void> adminGrant(Long loginId, Long targetId) {
        UserRole role = findUser(loginId).getRole();
        if (!role.equals(UserRole.ADMIN)) {
            throw new ApplicationException(ErrorCode.NO_PERMISSION_ACTION);
        }

        findUser(targetId).adminGrant();

        return ApiResponse.success("유저 권한 변경에 성공했습니다.");
    }
}
