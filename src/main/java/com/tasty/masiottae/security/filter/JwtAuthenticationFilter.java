package com.tasty.masiottae.security.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.dto.AccountLoginRequest;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtToken;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import com.tasty.masiottae.security.jwt.JwtTokenResponse;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    private final ObjectMapper objectMapper = getObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response)
        throws AuthenticationException {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;
        try {
            AccountLoginRequest accountLoginRequest = new ObjectMapper().readValue(
                request.getInputStream(), AccountLoginRequest.class);
            usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                accountLoginRequest.email(), accountLoginRequest.password());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain chain, Authentication authentication) throws IOException {
        AccountDetail account = (AccountDetail) authentication.getPrincipal();
        JwtTokenResponse totalResponse = makePayload(account);

        response.setContentType(APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), totalResponse);
    }

    private JwtTokenResponse makePayload(AccountDetail account) {
        JwtToken token = jwtTokenProvider.generatedAccountToken(account);
        AccountFindResponse accountFindResponse =
            new AccountFindResponse(account.getId(),
                account.getImage(),
                account.getNickName(),
                account.getUsername(),
                account.getSnsAccount(),
                account.getCreatedAt(),
                account.getMenuCount());
        return new JwtTokenResponse(token, accountFindResponse);
    }

    private ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

}
