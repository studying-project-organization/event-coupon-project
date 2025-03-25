package com.plusproject.domain.user.service;

import com.plusproject.common.dto.ApiResponse;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.dto.request.CreateUserRequest;
import com.plusproject.domain.user.dto.request.LoginRequest;
import com.plusproject.domain.user.entity.Users;
import com.plusproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepo;

    public ApiResponse<Void> createUser(CreateUserRequest req) {
        Users users = new Users(req);
        String email = req.getEmail();

        userRepo.save(users);
        return ApiResponse.success(req.getNickname() + " 회원가입 성공");
    }

    public Users findUser(Long id) {
        return userRepo.findByIdOrElseThrow(id, ErrorCode.NOT_FOUND_USER);
    }

    private ApiResponse<Void> login(Long loginId, LoginRequest req){
        Users user = findUser(loginId);
    }
}
