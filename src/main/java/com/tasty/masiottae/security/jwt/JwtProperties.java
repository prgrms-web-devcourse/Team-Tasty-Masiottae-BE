package com.tasty.masiottae.security.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt")
@Configuration
@RequiredArgsConstructor
@Getter
@Setter
public class JwtProperties {

    private final String secret;

    private final int expirationTime;

    private final String tokenPrefix;

    private final String headerString;
}
