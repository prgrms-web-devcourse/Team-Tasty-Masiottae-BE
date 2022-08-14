package com.tasty.masiottae.account.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import java.time.LocalDateTime;

@JsonRootName(value = "account")
public record AccountFindResponse(
        Long id,
        String image,
        String nickName,
        String email,
        String snsAccount,
        LocalDateTime createdAt,
        Integer menuCount
) {

}
