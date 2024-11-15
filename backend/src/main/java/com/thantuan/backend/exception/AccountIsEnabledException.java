package com.thantuan.backend.exception;

public class AccountIsEnabledException extends RuntimeException {
    public AccountIsEnabledException(String message) {
        super(message);
    }
}
