package com.tasty.masiottae.account.dto;

import java.time.LocalDateTime;

public record AccountFindResponse(
        Long id,
        String nickname,
        String imgUrl,
        String email,
        LocalDateTime createdAt,
        int menuCount
) {

}
