package com.tasty.masiottae.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String message = exception.getMessage();
        if (exception instanceof BadCredentialsException) {
            message = "비밀번호가 일치하지 않아요.";
        } else if (exception instanceof UsernameNotFoundException
                || exception instanceof InternalAuthenticationServiceException) {
            message = "존재하지 않는 회원이에요.";
        }
        setResponse(response, message);
    }

    private void setResponse(HttpServletResponse response, String message) throws IOException {
        response.setHeader("error", message);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
