package com.tasty.masiottae.security.jwt;

import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor
public record JwtTokenResponse(String token, Date expirationTime) {

}
