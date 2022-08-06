package com.tasty.masiottae.common.exception;

import static com.tasty.masiottae.common.exception.HibernateSqlErrorCode.UNKOWN;
import static com.tasty.masiottae.common.exception.HibernateSqlErrorCode._1048;
import static com.tasty.masiottae.common.exception.HibernateSqlErrorCode._1062;

import com.tasty.masiottae.common.exception.custom.NotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage(),
              HttpServletResponse.SC_BAD_REQUEST, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage(),
                HttpServletResponse.SC_BAD_REQUEST, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
        
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            final NotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage(),
                HttpServletResponse.SC_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleSQLIntegrityConstraintViolationException(
        final SQLIntegrityConstraintViolationException e) {
        StringBuilder sb = new StringBuilder();
        HibernateSqlErrorCode code = HibernateSqlErrorCode.findCode(e.getErrorCode());
        Optional<String> keyword = extractKeyword(e.getMessage());

        switch (code) {
            case _1048 -> sb.append(_1048.getMessage());
            case _1062 -> sb.append(_1062.getMessage());
            default -> {
                sb.append(UNKOWN.getMessage());
                log.error(e.getMessage());
            }
        }

        keyword.ifPresent(s -> sb.append(" -> ").append(s));

        final ErrorResponse response = ErrorResponse.of(sb.toString(),
            HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    private Optional<String> extractKeyword(String message) {
        Pattern pattern = Pattern.compile("['](.*?)[']");
        Matcher matcher = pattern.matcher(message);

        return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
    }

}

