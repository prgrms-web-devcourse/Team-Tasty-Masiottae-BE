package com.tasty.masiottae.security.jwt;

import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtToken {

    private final String accessToken;

    private final Date expirationTime;

}
