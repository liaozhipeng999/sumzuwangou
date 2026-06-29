package com.mall.exception;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidException(MethodArgumentNotValidException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        StringBuilder message = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            message.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        result.put("message", message.toString());
        return result;
    }

    @ExceptionHandler(BindException.class)
    public Map<String, Object> handleBindException(BindException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        StringBuilder message = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            message.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        result.put("message", message.toString());
        return result;
    }

    @ExceptionHandler(RuntimeException.class)
    public Map<String, Object> handleRuntimeException(RuntimeException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("message", e.getMessage());
        return result;
    }
}
