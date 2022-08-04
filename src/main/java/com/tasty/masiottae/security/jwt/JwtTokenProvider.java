package com.tasty.masiottae.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public JwtToken generatedAccountToken(UserDetails userDetails) {

        Algorithm algorithm = Algorithm.HMAC256(
            jwtProperties.getSecret().getBytes());
        Date expirationDate = new Date(
            System.currentTimeMillis() + jwtProperties.getExpirationTime());
        String token = JWT.create()
            .withSubject(userDetails.getUsername())
            .withExpiresAt(expirationDate)
            .withClaim("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
            .sign(algorithm);

        return new JwtToken(jwtProperties.getTokenPrefix() + token, expirationDate);
    }

    public DecodedJWT verifyJwtToken(String token) {
        if (token == null || !token.startsWith(jwtProperties.getTokenPrefix())) {
            throw new JWTVerificationException("부적절한 토큰값입니다.");
        }
        token = token.substring(jwtProperties.getTokenPrefix().length());
        Algorithm algorithm = Algorithm.HMAC256(jwtProperties.getSecret().getBytes());
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        return jwtVerifier.verify(token);
    }

    public String getToken(HttpServletRequest request) {
        return request.getHeader(jwtProperties.getHeaderString());
    }

    public String getPrefix() {
        return jwtProperties.getTokenPrefix();
    }

}
