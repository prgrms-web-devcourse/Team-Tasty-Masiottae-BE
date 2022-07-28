package com.tasty.masiottae.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.auth.AccountDetailService;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private final AccountDetailService accountDetailService;

    public JwtTokenResponse generatedAccountToken(AccountDetail accountDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", accountDetails.getAuthorities());
        claims.put("email", accountDetails.getUsername());
        claims.put("nickName", accountDetails.getNickname());

        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        Date expirationDate = new Date(System.currentTimeMillis() + jwtProperties.getExpirationTime());
        String token = JWT.create()
                .withSubject(accountDetails.getUsername())
                .withExpiresAt(expirationDate)
                .withClaim("roles", accountDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);

        return new JwtTokenResponse(token, expirationDate);
    }

}
