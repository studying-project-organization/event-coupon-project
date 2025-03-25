package com.plusproject.domain.user.repository;

import com.plusproject.common.repository.BaseRepository;
import com.plusproject.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
