package com.plusproject.common.lock;

public interface DistributedLockManager {

    void executeWithLock(Long key, Runnable task) throws InterruptedException;

}
