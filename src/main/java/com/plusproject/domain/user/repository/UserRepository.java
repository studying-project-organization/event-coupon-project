package com.plusproject.domain.user.repository;

import com.plusproject.domain.user.entity.Users;
import com.plusproject.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends BaseRepository<Users, Long> {
}
