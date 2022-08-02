package com.tasty.masiottae.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasty.masiottae.account.dto.AccountLoginRequest;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtToken;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
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
                    accountLoginRequest.email(), accountLoginRequest.password());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AccountDetail account = (AccountDetail) authentication.getPrincipal();

        JwtToken token = jwtTokenProvider.generatedAccountToken(account);

        request.getSession().setAttribute("id", account.getId());
        request.getSession().setAttribute("token", token);
        setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler("/login_success"));
        super.successfulAuthentication(request, response, chain, authentication);
    }

}
