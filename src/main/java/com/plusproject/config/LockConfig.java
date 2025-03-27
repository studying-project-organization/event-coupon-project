package com.plusproject.config;

import com.plusproject.common.lock.DistributedLockManager;
import com.plusproject.common.lock.RedissonDistributedLockManager;
import com.plusproject.domain.lock.lettuce.service.LettuceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockConfig {

    @Bean("lettuceLock")
    public DistributedLockManager lettuceLockManager(LettuceService lettuce) {
        return lettuce;
    }

    @Bean("redissonLock")
    public DistributedLockManager redissonLockManager(RedissonDistributedLockManager redisson) {
        return redisson;
    }

}
