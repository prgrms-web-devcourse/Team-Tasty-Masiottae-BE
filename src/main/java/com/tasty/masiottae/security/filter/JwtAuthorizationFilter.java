package com.tasty.masiottae.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.TokenCache;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final TokenCache tokenCache;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/login")) {
            filterChain.doFilter(request, response);
        } else {
            String token = jwtTokenProvider.getToken(request);
            if (Objects.nonNull(token) && token.startsWith(jwtTokenProvider.getPrefix())) {
                try {
                    DecodedJWT decodedJWT = jwtTokenProvider.verifyJwtToken(token);
                    checkBlackList(decodedJWT.getSubject(), token);
                    Authentication authentication = getAuthentication(decodedJWT);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.info("권한 확인 -> {}", decodedJWT.getSubject());
                    filterChain.doFilter(request, response);
                } catch (JWTVerificationException e) {
                    log.info("권한 없는 유저 접근");
                    respondWithError(response, e.getMessage());
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }

    private Authentication getAuthentication(DecodedJWT decodedJWT) {
        String email = decodedJWT.getSubject();
        String[] rolesString = decodedJWT.getClaim("roles").asArray(String.class);
        Long id = decodedJWT.getClaim("id").asLong();
        Collection<GrantedAuthority> roles = new ArrayList<>();
        for (String roleString : rolesString) {
            roles.add(new SimpleGrantedAuthority(roleString));
        }
        Account account = Account.createAccount(id, email, null, null, roles.toString());
        AccountDetail accountDetail = new AccountDetail(account);
        return new UsernamePasswordAuthenticationToken(
                accountDetail, null, roles);
    }

    private void checkBlackList(String username, String accessToken) {
        if (tokenCache.isAccessTokenInBlackList(username, accessToken)) {
            throw new JWTVerificationException("사용이 불가한 토큰");
        }
    }

    private void respondWithError(HttpServletResponse response, String message) throws IOException {
        response.setHeader("error", message);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

}
