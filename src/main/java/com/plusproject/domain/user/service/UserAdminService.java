package com.plusproject.domain.user.service;

import com.plusproject.common.exception.ErrorCode;
import com.plusproject.domain.user.dto.request.ChangeUserRoleRequest;
import com.plusproject.domain.user.entity.User;
import com.plusproject.domain.user.enums.UserRole;
import com.plusproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAdminService {

    private final UserRepository userRepository;

    @Transactional
    public void changeUserRole(long userId, ChangeUserRoleRequest request) {
        User user = userRepository.findByIdOrElseThrow(userId, ErrorCode.NOT_FOUND_USER);
        user.updateRole(UserRole.of(request.getRole()));
    }
}
