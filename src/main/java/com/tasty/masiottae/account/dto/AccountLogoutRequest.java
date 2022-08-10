package com.tasty.masiottae.account.dto;

public record AccountLogoutRequest(
    String email,
    String accessToken,
    String refreshToken
) {

}
