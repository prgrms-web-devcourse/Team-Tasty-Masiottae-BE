package com.tasty.masiottae.security.jwt;

import com.tasty.masiottae.account.dto.AccountFindResponse;

public record JwtTokenResponse(
        JwtToken jwtToken,
        AccountFindResponse accountFindResponse) {
}
