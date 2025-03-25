package com.plusproject.domain.user.repository;

import com.plusproject.domain.user.entity.Users;
import com.plusproject.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends BaseRepository<Users, Long> {
    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);
}
