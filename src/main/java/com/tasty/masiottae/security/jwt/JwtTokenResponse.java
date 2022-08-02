package com.tasty.masiottae.security.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tasty.masiottae.account.dto.AccountFindResponse;

public record JwtTokenResponse(
        @JsonProperty(value = "token")
        JwtToken jwtToken,
        @JsonProperty(value = "account")
        AccountFindResponse accountFindResponse) {
}
