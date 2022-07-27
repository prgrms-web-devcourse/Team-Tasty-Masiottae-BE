package com.tasty.masiottae.common.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
public class ErrorResponse {

    private String message;
    private int status;
    private List<ValidationError> errors;

    private ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(String message, int status, List<ValidationError> errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
    }

    public static ErrorResponse of(String message, int status) {
        return new ErrorResponse(message, status);
    }

    public static ErrorResponse of(String message, int status, final BindingResult bindingResult) {
        return new ErrorResponse(message, status, ValidationError.of(bindingResult));
    }

    @Getter
    public static class ValidationError {

        private String field;
        private String value;
        private String reason;

        private ValidationError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<ValidationError> of(BindingResult bindingResult) {
            List<FieldError> fieldErrors =
                    bindingResult.getFieldErrors();
            return fieldErrors.stream().map(fieldError ->
                    new ValidationError(
                            fieldError.getField(),
                            fieldError.getRejectedValue() == null ? ""
                                    : fieldError.getRejectedValue().toString(),
                            fieldError.getDefaultMessage()
                    )
            ).collect(Collectors.toList());
        }
    }
}
