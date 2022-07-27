package com.tasty.masiottae.common.exception;

import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final RuntimeException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

}

