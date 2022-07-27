package com.tasty.masiottae.common.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ErrorResponse {

    private String message;
    private int status;
    private List<FieldError> errors;

    private ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(String message, int status, List<FieldError> errors) {
        this.message = message;
        this.status = status;
        this.errors = errors;
    }

    public static ErrorResponse of(String message, int status) {
        return new ErrorResponse(message, status);
    }

    public static ErrorResponse of(String message, int status, final BindingResult bindingResult) {
        return new ErrorResponse(message, status, FieldError.of(bindingResult));
    }

    @Getter
    public static class FieldError {

        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(BindingResult bindingResult) {
            List<org.springframework.validation.FieldError> fieldErrors =
                    bindingResult.getFieldErrors();
            return fieldErrors.stream().map(fieldError ->
                    new FieldError(
                            fieldError.getField(),
                            fieldError.getRejectedValue() == null ? ""
                                    : fieldError.getRejectedValue().toString(),
                            fieldError.getDefaultMessage()
                    )
            ).collect(Collectors.toList());
        }
    }
}
