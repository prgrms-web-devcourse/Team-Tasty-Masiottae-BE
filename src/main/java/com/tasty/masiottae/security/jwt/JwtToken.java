package com.tasty.masiottae.security.jwt;

import com.fasterxml.jackson.annotation.JsonRootName;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonRootName("token")
public class JwtToken {

    private final String accessToken;

    private final Date expirationTime;

}
