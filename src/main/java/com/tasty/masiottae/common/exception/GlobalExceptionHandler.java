package com.tasty.masiottae.common.exception;

import static com.tasty.masiottae.common.exception.ErrorMessage.MEDIA_TYPE_NOT_SUPPORTED;
import static com.tasty.masiottae.common.exception.ErrorMessage.METHOD_ARGUMENT_NOT_VALID;
import static com.tasty.masiottae.common.exception.ErrorMessage.MISSING_SERVLET_REQUEST_PART;
import static com.tasty.masiottae.common.exception.ErrorMessage.NO_HANDLER_FOUND_EXCEPTION;
import static com.tasty.masiottae.common.exception.ErrorMessage.REQUEST_METHOD_NOT_SUPPORTED;
import static com.tasty.masiottae.common.exception.HibernateSqlErrorCode.UNKOWN;
import static com.tasty.masiottae.common.exception.HibernateSqlErrorCode._1048;
import static com.tasty.masiottae.common.exception.HibernateSqlErrorCode._1062;

import com.tasty.masiottae.common.exception.custom.ForbiddenException;
import com.tasty.masiottae.common.exception.custom.NotFoundException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(final ForbiddenException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), HttpServletResponse.SC_FORBIDDEN);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            final IllegalArgumentException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage(),
                HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(final BindException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(),
                HttpServletResponse.SC_BAD_REQUEST, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
        final EntityNotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage(),
            HttpServletResponse.SC_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
        final NotFoundException e) {
        final ErrorResponse response = ErrorResponse.of(e.getMessage(),
            HttpServletResponse.SC_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<ErrorResponse> handleRequestRejectedException(
        final RequestRejectedException e) {

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
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
        final MethodArgumentNotValidException e) {
        final ErrorResponse response = ErrorResponse.of(METHOD_ARGUMENT_NOT_VALID.getMessage(),
            HttpServletResponse.SC_BAD_REQUEST, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(
        final MissingServletRequestPartException e) {
        StringBuilder sb = new StringBuilder();
        sb
            .append(MISSING_SERVLET_REQUEST_PART.getMessage())
            .append(" -> ")
            .append(e.getRequestPartName());
        final ErrorResponse response = ErrorResponse.of(sb.toString(),
            HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupportedException(
        final HttpMediaTypeNotSupportedException e) {
        StringBuilder sb = new StringBuilder();
        sb
            .append(e.getContentType())
            .append(MEDIA_TYPE_NOT_SUPPORTED.getMessage());
        final ErrorResponse response = ErrorResponse.of(sb.toString(),
            HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        return new ResponseEntity<>(response, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
        final NoHandlerFoundException e) {
        StringBuilder sb = new StringBuilder();
        sb
            .append(NO_HANDLER_FOUND_EXCEPTION.getMessage())
            .append(" -> ")
            .append(e.getHttpMethod())
            .append(" ")
            .append(e.getRequestURL());
        final ErrorResponse response = ErrorResponse.of(sb.toString(),
            HttpServletResponse.SC_NOT_FOUND);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
        final HttpRequestMethodNotSupportedException e) {
        StringBuilder sb = new StringBuilder();
        sb
            .append(e.getMethod())
            .append(REQUEST_METHOD_NOT_SUPPORTED.getMessage())
            .append(" -> ")
            .append(Arrays.asList(e.getSupportedMethods()));
        final ErrorResponse response = ErrorResponse.of(sb.toString(),
            HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(final AccessDeniedException e) {
        ErrorResponse response = ErrorResponse.of(e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    private Optional<String> extractKeyword(String message) {
        Pattern pattern = Pattern.compile("['](.*?)[']");
        Matcher matcher = pattern.matcher(message);

        return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
    }

}

