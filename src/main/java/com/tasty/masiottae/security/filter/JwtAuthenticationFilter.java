package com.tasty.masiottae.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.dto.AccountLoginRequest;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtToken;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import com.tasty.masiottae.security.jwt.JwtTokenResponse;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response)
            throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
        try {
            AccountLoginRequest accountLoginRequest = new ObjectMapper().readValue(
                    request.getInputStream(), AccountLoginRequest.class);
            usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    accountLoginRequest.loginId(), accountLoginRequest.loginPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        return authenticate;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain, Authentication authentication) throws IOException {
        UserDetails account = (UserDetails) authentication.getPrincipal();

        JwtToken token = jwtTokenProvider.generatedAccountToken(account);
        AccountDetail accountDetails = (AccountDetail) account;
        AccountFindResponse accountFindResponse =
                new AccountFindResponse(accountDetails.getId(), accountDetails.getNickname(),
                        accountDetails.getImageUrl(), accountDetails.getUsername(), accountDetails.getCreatedAt(),
                        0);
        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse(token, accountFindResponse);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), jwtTokenResponse);
    }

}
