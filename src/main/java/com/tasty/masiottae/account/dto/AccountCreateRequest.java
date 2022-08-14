package com.tasty.masiottae.account.dto;

public record AccountCreateRequest(
        String email,
        String password,
        String nickName,
        String snsAccount) {

}
