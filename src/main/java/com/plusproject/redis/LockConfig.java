package com.plusproject.redis;

import com.plusproject.redis.lettuce.LettuceService;
import com.plusproject.redis.redisson.RedissonLockImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockConfig {

    @Bean("lettuceLock")
    public DistributedLockManager lettuceLockManager(LettuceService lettuceService) {
        return lettuceService;
    }

    @Bean("redissonLock")
    public DistributedLockManager redissonLockManager(RedissonLockImpl redissonLock) {
        return redissonLock;
    }
}
