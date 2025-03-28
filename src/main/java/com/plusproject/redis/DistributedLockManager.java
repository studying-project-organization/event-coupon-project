package com.plusproject.redis;

public interface DistributedLockManager {

    void executeWithLock(Long key, Runnable task) throws InterruptedException;

}
