package com.tasty.masiottae.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    // common
    UNCAUGHT_ERROR("처리되지 않은 에러입니다. 담당자에게 문의해 주세요."),
    INVALID_INPUT_VALUE("잘못된 데이터를 입력하였습니다."),
    MEDIA_TYPE_NOT_SUPPORTED(" 은(는) 지원하지 않는 미디어 타입입니다."),
    METHOD_ARGUMENT_NOT_VALID("정의되지 않은 요청 정보가 있습니다."),
    MISSING_SERVLET_REQUEST_PART("요청 파트 중 일부가 누락되었습니다. 다음을 포함하여 재요청하세요."),
    NO_HANDLER_FOUND_EXCEPTION("해당 요청을 처리할 수 없습니다. 요청 url을 확인해 주세요."),
    REQUEST_METHOD_NOT_SUPPORTED(" 은(는) 지원하지 않습니다. 다음의 메소드로 요청하세요."),


    // s3
    IMAGE_SAVE_ERROR("이미지 업로드에 문제가 발생하였습니다."),

    // taste
    NOT_FOUND_TASTE("존재하지 않는 맛입니다."),
    NOT_FOUND_SOME_TASTE("존재하지 않는 맛이 포함되어 있습니다."),

    // franchise
    NOT_FOUND_FRANCHISE("존재하지 않는 프랜차이즈입니다."),

    // account
    NOT_FOUND_ACCOUNT("존재하지 하지 않는 유저입니다."),

    // menu
    INVALID_MENU_ORDER_COND("존재하지 않는 메뉴 정렬 조건입니다."),
    NOT_FOUND_MENU("존재하지 않는 메뉴입니다."),

    // comment
    NO_COMMENT_CONTENT("댓글 내용을 입력해주세요.");

    private final String message;

}
