package com.example.PropertyDemo;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiError {

    public LocalDateTime timestamp = LocalDateTime.now();
    public Map<String, String> errors;
    public String status;

    public ApiError(Map<String, String> errors, String status) {
        this.errors = errors;
        this.status = status;
    }

}
