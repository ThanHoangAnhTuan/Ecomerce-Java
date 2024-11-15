package com.thantuan.backend.exception;

import com.thantuan.backend.dto.Response;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.BindException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = UsernameNotFoundException.class)
    public ResponseEntity<Response> handleUsernameNotFoundException(
            @NotNull UsernameNotFoundException ex) {
        log.error("UsernameNotFoundException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(value = CategoryNotFoundException.class)
    public ResponseEntity<Response> handleCategoryNotFoundException(
            @NotNull CategoryNotFoundException ex) {
        log.error("CategoryNotFoundException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(value = ProductNotFoundException.class)
    public ResponseEntity<Response> handleProductNotFoundException(
            @NotNull ProductNotFoundException ex) {
        log.error("ProductNotFoundException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(NOT_FOUND.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, NOT_FOUND);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<Response> handleMissingServletRequestParameterException(
            @NotNull MissingServletRequestParameterException ex) {
        log.error("MissingServletRequestParameterException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(value = AccountIsEnabledException.class)
    public ResponseEntity<Response> handleAccountIsEnabledException(
            @NotNull AccountIsEnabledException ex) {
        log.error("AccountIsEnabledException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    private List<String> orderMessageErrors (List<String> errors) {
        List<String> orderedErrors = new ArrayList<>();
        if (errors.contains("Username is required!")) {
            orderedErrors.add("Username is required!");
        }
        if (errors.contains("Email is required!")) {
            orderedErrors.add("Email is required!");
        }
        if (errors.contains("Email should be valid!")) {
            orderedErrors.add("Email should be valid!");
        }
        if (errors.contains("Password must contain at least one special character.")) {
            orderedErrors.add("Password must contain at least one special character.");
        }
        if (errors.contains("Password must contain at least one digit.")) {
            orderedErrors.add("Password must contain at least one digit.");
        }
        if (errors.contains("Password must contain at least one uppercase letter.")) {
            orderedErrors.add("Password must contain at least one uppercase letter.");
        }
        if (errors.contains("Password must contain at least one lowercase letter.")) {
            orderedErrors.add("Password must contain at least one lowercase letter.");
        }
        if (errors.contains("Password must be at least 8 characters long.")) {
            orderedErrors.add("Password must be at least 8 characters long.");
        }
        if (errors.contains("Password is required!")) {
            orderedErrors.add("Password is required!");
        }

        for (String error : errors) {
            if (!orderedErrors.contains(error)) {
                orderedErrors.add(error);
            }
        }
        return orderedErrors;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Response> handleMethodArgumentNotValidException(
            @NotNull MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());
        List<String> errors = new ArrayList<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });
        List<String> orderedErrors = orderMessageErrors(errors);
        Response errorResponse = Response.builder()
                .status(BAD_REQUEST.value())
                .validationErrors(orderedErrors)
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<Response> handleInvalidTokenException(@NotNull InvalidTokenException ex) {
        log.error("InvalidTokenException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(UNAUTHORIZED.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(value = EmailAlreadyExistsException.class)
    public ResponseEntity<Response> handleEmailAlreadyExistsException(
            @NotNull EmailAlreadyExistsException ex) {
        log.error("EmailAlreadyExistsException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(CONFLICT.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, CONFLICT);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<Response> handleBadCredentialsException(
            @NotNull BadCredentialsException ex) {
        log.error("BadCredentialsException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(UNAUTHORIZED.value())
                .message("Invalid password")
                .build();
        return new ResponseEntity<>(errorResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(FORBIDDEN.value())
                .message("You do not have permission to access this resource")
                .build();
        return new ResponseEntity<>(errorResponse, FORBIDDEN);
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Response> handleHttpMessageNotReadableException(
            @NotNull HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Response> handleAllExceptions(@NotNull Exception ex) {
        log.error("Exception: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = CategoryNameNotNullException.class)
    public ResponseEntity<Response> handleCategoryNameNotNullException(
            @NotNull CategoryNameNotNullException ex) {
        log.error("CategoryNameNotNullException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(value = CategoryAlreadyExistsException.class)
    public ResponseEntity<Response> handleCategoryAlreadyExistsException(
            @NotNull CategoryAlreadyExistsException ex) {
        log.error("CategoryAlreadyExistsException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(CONFLICT.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, CONFLICT);
    }

    @ExceptionHandler(value = FileNotNullException.class)
    public ResponseEntity<Response> handleFileNotNullException(@NotNull FileNotNullException ex) {
        log.error("FileNotNullException: {}", ex.getMessage());
        Response errorResponse = Response.builder()
                .status(BAD_REQUEST.value())
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }
}
