package com.tasty.masiottae.common.exception;

import static com.tasty.masiottae.common.exception.ErrorMessage.UNCAUGHT_ERROR;

import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order
@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ErrorResponse> handleUncaughtException(final Exception e) {
        final ErrorResponse response = ErrorResponse.of(UNCAUGHT_ERROR.getMessage(),
            HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
