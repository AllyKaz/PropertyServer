package com.example.PropertyDemo;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {

    public LocalDateTime timestamp = LocalDateTime.now();
    public List<String> errors;
    public String status;

    public ApiError(List<String> errors, String status) {
        this.errors = errors;
        this.status = status;
    }

}
