package com.tasty.masiottae.security.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JwtToken(
    @JsonProperty(value = "accessToken")
    JwtAccessToken jwtAccessToken,
    @JsonProperty(value = "refreshToken")
    JwtRefreshToken jwtRefreshToken) {

}
