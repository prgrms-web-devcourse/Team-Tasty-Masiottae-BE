package com.tasty.masiottae.account.domain;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum CheckProperty {

    NICK_NAME("nickName", "이미 존재하는 닉네임입니다."),
    EMAIL("email", "이미 존재하는 이메일입니다."),
    INVALID("", "잘못된 값에 대한 요청입니다.");

    private final String fieldName;
    private final String errorMessage;

    CheckProperty(String fieldName, String errorMessage) {
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    public static CheckProperty findProperty(String fieldName) {
        return Arrays.stream(CheckProperty.values())
                .filter(prop -> prop.fieldName.equals(fieldName))
                .findFirst()
                .orElse(INVALID);
    }

}
