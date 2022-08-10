package com.tasty.masiottae.account.repository;

import com.tasty.masiottae.security.jwt.JwtAccessToken;
import com.tasty.masiottae.security.jwt.JwtProperties;
import com.tasty.masiottae.security.jwt.JwtRefreshToken;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TokenCache {

    private final ConcurrentMap<String, JwtRefreshToken> refreshTokenCache;
    private final ConcurrentMap<String, List<JwtAccessToken>> accessTokenCache;

    public TokenCache(@Autowired JwtProperties jwtProperties) {
        this.refreshTokenCache = new ConcurrentHashMap<>();
        this.accessTokenCache = new ConcurrentHashMap<>();
    }

    public boolean holdRefreshToken(String username, String refreshToken) {
        JwtRefreshToken token = refreshTokenCache.get(username);
        return token != null && token.getToken().equals(refreshToken);
    }

    public boolean isAccessTokenInBlackList(String username, String accessToken) {
        List<JwtAccessToken> tokens = accessTokenCache.get(username);
        return tokens != null && tokens.stream()
            .anyMatch(jwtAccessToken -> jwtAccessToken.getToken().equals(accessToken));
    }

    public void registerRefreshToken(String username, JwtRefreshToken refreshToken) {
        refreshTokenCache.put(username, refreshToken);
    }

    public void registerRefreshToken(String username, String refreshToken, Date expirationDate) {
        refreshTokenCache.put(username, new JwtRefreshToken(refreshToken, expirationDate));
    }

    public void removeRefreshToken(String username) {
        refreshTokenCache.remove(username);
    }

    public void blockAccessToken(String username, JwtAccessToken accessToken) {
        List<JwtAccessToken> accessTokenList = accessTokenCache.getOrDefault(username,
            new LinkedList<>());
        accessTokenList.add(accessToken);
        accessTokenCache.put(username, accessTokenList);
    }

    public void blockAccessToken(String username, String accessToken, Date expirationDate) {
        List<JwtAccessToken> accessTokenList = accessTokenCache.getOrDefault(username,
            new LinkedList<>());
        accessTokenList.add(new JwtAccessToken(accessToken, expirationDate));
        accessTokenCache.put(username, accessTokenList);
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void invalidateRefreshToken() {
        for (String username : refreshTokenCache.keySet()) {
            JwtRefreshToken refreshToken = refreshTokenCache.get(username);

            if (TimerUtils.isExpired(refreshToken.getExpirationDate())) {
                refreshTokenCache.remove(username);
            }
        }

        log.info("Current Refresh Cache Size -> {}", refreshTokenCache.size());
    }

    @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
    public void invalidateAccessToken() {
        for (String username : accessTokenCache.keySet()) {
            List<JwtAccessToken> accessTokens = accessTokenCache.get(username);

            if (accessTokens.isEmpty()) {
                accessTokenCache.remove(username);
            }

            List<JwtAccessToken> update = new LinkedList<>();
            for (JwtAccessToken token : accessTokens) {
                if (!TimerUtils.isExpired(token.getExpirationDate())) {
                    update.add(token);
                }
            }
            accessTokenCache.put(username, update);
        }

        log.info("Current Access Cache Size -> {}", accessTokenCache.size());
    }

}
