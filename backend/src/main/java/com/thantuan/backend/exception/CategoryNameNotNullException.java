package com.thantuan.backend.exception;

public class CategoryNameNotNullException extends RuntimeException {
    public CategoryNameNotNullException(String message) {
        super(message);
    }
}