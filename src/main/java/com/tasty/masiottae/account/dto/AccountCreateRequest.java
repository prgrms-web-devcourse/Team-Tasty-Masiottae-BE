package com.tasty.masiottae.account.dto;

public record AccountCreateRequest(
        String email,
        String nickname,
        String password,
        String imgUrl,
        String snsAccount
) {

}
