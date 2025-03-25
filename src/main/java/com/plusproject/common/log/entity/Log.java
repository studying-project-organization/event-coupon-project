package com.plusproject.common.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "logs")
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level; // 로그 레벨 (INFO, ERROR 등)
    private String message; // 로그 메시지
    private LocalDateTime timestamp; // 타임스탬프
}
