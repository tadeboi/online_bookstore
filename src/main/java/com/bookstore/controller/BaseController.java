package com.bookstore.controller;

import com.bookstore.security.SecurityUtils;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseController {
    protected static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String CURRENT_TIME = "2025-03-23 19:50:06";
    protected static final String CURRENT_USER = "tadeboi";

    protected LocalDateTime getCurrentDateTime() {
        return LocalDateTime.parse(CURRENT_TIME, DATE_FORMATTER);
    }

    protected String getCurrentUsername() {
        return CURRENT_USER;
    }

    protected Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    protected <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }
}