package com.tasty.masiottae.config;

import java.util.ArrayList;
import java.util.Collection;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.domain.Role;
import com.tasty.masiottae.security.auth.AccountDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockAccountSecurityContextFactory implements
        WithSecurityContextFactory<WithMockAccount> {

    @Override
    public SecurityContext createSecurityContext(WithMockAccount annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Account account = Account.createAccount("test", null, "test", Role.ACCOUNT.getAuthority());
        AccountDetail accountDetail = new AccountDetail(account);
        Collection<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(Role.ACCOUNT.getAuthority()));

        Authentication auth = new UsernamePasswordAuthenticationToken(accountDetail, null, roles);
        context.setAuthentication(auth);
        return context;
    }
}
