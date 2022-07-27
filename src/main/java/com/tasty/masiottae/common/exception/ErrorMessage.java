package com.tasty.masiottae.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    INVALID_INPUT_VALUE("잘못된 데이터를 입력하였습니다."),
    NOT_FOUND_MENU_ERR("존재하지 않는 메뉴입니다.");

    private final String message;
}
