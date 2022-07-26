package com.tasty.masiottae.account.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    ACCOUNT("ROLE_ACCOUNT", "일반 유저"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String authority;
    private final String description;

}