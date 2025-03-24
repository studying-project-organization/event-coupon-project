package com.plusproject.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime toLocalDateTime(String date){
        return LocalDateTime.parse(date, formatter);
    }

    public String toString(LocalDateTime endDate) {
        return endDate.format(formatter);
    }
}
