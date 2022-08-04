package com.tasty.masiottae.security.auth;

import java.time.LocalDateTime;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class AccountDetail implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String nickName;
    private final String image;
    private final String snsAccount;
    private final Integer menuCount;
    private final Collection<SimpleGrantedAuthority> authorities;
    private final LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public String getImage() {
        return image;
    }

    public String getSnsAccount() {
        return snsAccount;
    }

    public Integer getMenuCount() {
        return menuCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
