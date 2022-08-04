package com.tasty.masiottae.menu.enums;

import com.tasty.masiottae.common.exception.ErrorMessage;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MenuSortCond {
    RECENT("recent"), LIKE("like"), COMMENT("comment");

    private final String urlValue;

    public static MenuSortCond find(String urlValue) {
        return Arrays.stream(values()).filter(cond -> cond.getUrlValue().equals(urlValue)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(ErrorMessage.INVALID_MENU_ORDER_COND.getMessage()));
    }
}
