package com.tasty.masiottae.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = jwtTokenProvider.getToken(request);
        try {
            DecodedJWT decodedJWT = jwtTokenProvider.verifyJwtToken(token);
            Authentication authentication = getAuthentication(decodedJWT);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JWTVerificationException e) {
            logger.info("유효하지 않은 토큰 접근입니다." + token);
        }
        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(DecodedJWT decodedJWT) {
        String email = decodedJWT.getSubject();
        String[] rolesString = decodedJWT.getClaim("role").asArray(String.class);
        Collection<GrantedAuthority> roles = new ArrayList<>();
        for (String roleString : rolesString) {
            roles.add(new SimpleGrantedAuthority(roleString));
        }

        return new UsernamePasswordAuthenticationToken(
                email, null, roles);
    }
}
