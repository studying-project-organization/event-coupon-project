package com.plusproject.domain.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/healthy")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("현재 시간 " + LocalDateTime.now() + " / 현재 서버가 정상 작동 중입니다!");
    }

}
