package com.tasty.masiottae.account.dto;

import java.time.LocalDateTime;

public record AccountUpdateRequest(
        String nickname,
        String imgUrl
) {

}
