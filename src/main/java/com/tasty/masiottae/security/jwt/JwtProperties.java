package com.tasty.masiottae.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt")
@Configuration
@Getter
@Setter
public class JwtProperties {

    private String secret;

    private String expirationTime;

    private String tokenPrefix;

    private String headerString;

    public int getExpirationTime() {
        return Integer.parseInt(expirationTime);
    }
}
