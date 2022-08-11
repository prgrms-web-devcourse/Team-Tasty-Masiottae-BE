package com.tasty.masiottae.common.exception.custom;

import org.springframework.web.bind.annotation.ResponseStatus;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
