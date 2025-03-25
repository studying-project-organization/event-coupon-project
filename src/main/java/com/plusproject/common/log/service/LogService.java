package com.plusproject.common.log.service;

import com.plusproject.common.log.entity.Log;
import com.plusproject.common.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogService {
    private final LogRepository logRepository;

    @Transactional
    public void log(String level, String message) {
        Log logEntry = new Log();
        logEntry.setLevel(level);
        logEntry.setMessage(message);
        logEntry.setTimestamp(LocalDateTime.now());

        logRepository.save(logEntry);
    }
}
