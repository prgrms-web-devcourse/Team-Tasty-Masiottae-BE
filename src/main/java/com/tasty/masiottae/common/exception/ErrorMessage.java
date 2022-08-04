package com.tasty.masiottae.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    // common
    UNCAUGHT_ERROR("처리되지 않은 에러입니다. 담당자에게 문의해 주세요."),
    INVALID_INPUT_VALUE("잘못된 데이터를 입력하였습니다."),

    // s3
    IMAGE_SAVE_ERROR("이미지 업로드에 문제가 발생하였습니다."),

    // taste
    NOT_FOUND_TASTE("존재하지 않는 맛입니다."),

    // franchise
    NOT_FOUND_FRANCHISE("존재하지 않는 프랜차이즈입니다."),

    // account
    NOT_FOUND_ACCOUNT("존재하지 하지 않는 유저입니다.");

    private final String message;

}
