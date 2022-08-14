package com.tasty.masiottae.account.dto;

public record AccountReIssueRequest(
    String email,
    String accessToken,
    String refreshToken
) {

}
