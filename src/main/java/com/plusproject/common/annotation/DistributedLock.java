package com.plusproject.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key(); // 락의 키로 사용할 값
    long waitTime() default 10000; // 락 획득 대기 시간 (ms)
    long lockTime() default 5000; // 락 유지 시간 (ms)
}