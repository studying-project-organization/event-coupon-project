package com.plusproject.domain.user.repository;

import com.plusproject.common.exception.ApplicationException;
import com.plusproject.common.exception.ErrorCode;
import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    default User findUserByEmailOrElseThrow(String email) {
        return findUserByEmail(email)
            .orElseThrow(() -> new ApplicationException(ErrorCode.NOT_FOUND_USER));
    }

}
