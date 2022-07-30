package com.tasty.masiottae.security.jwt;

import com.tasty.masiottae.account.dto.AccountFindResponse;
import java.util.Date;

public record JwtTokenResponse(
        JwtToken jwtToken,
        AccountFindResponse accountFindResponse) {
}
