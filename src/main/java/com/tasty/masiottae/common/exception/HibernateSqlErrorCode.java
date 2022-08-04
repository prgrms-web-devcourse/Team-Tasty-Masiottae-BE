package com.tasty.masiottae.common.exception;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HibernateSqlErrorCode {

    _1062(1062, "요청에 이미 존재하는 데이터가 포함되어 있습니다."),
    _1048(1048, "반드시 포함되어야 하는 데이터가 누락되었습니다."),
    UNKOWN(-1, "알 수 없는 DB 에러입니다. 담당자에게 문의하세요.");

    private final int code;
    private final String message;

    public static HibernateSqlErrorCode findCode(int inputCode) {
        return Arrays.stream(values())
            .filter(hibernateSqlErrorCode -> hibernateSqlErrorCode.code == inputCode)
            .findFirst()
            .orElse(UNKOWN);
    }

}
