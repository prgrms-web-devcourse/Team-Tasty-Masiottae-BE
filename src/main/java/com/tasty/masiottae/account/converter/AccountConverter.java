package com.tasty.masiottae.account.converter;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.security.auth.AccountDetail;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class AccountConverter {

    public AccountFindResponse toAccountFindResponse(Account account) {
        return new AccountFindResponse(
            account.getId(),
            account.getImage(),
            account.getNickName(),
            account.getEmail(),
            account.getSnsAccount(),
            account.getCreatedAt(),
            account.getMenuList().size()
        );
    }

    public AccountDetail toAccountDetail(Account account) {
        Collection<SimpleGrantedAuthority> authroties = new ArrayList<>();
        authroties.add(new SimpleGrantedAuthority(account.getRole().getAuthority()));

        return new AccountDetail(
            account.getId(),
            account.getEmail(),
            account.getPassword(),
            account.getNickName(),
            account.getImage(),
            account.getSnsAccount(),
            account.getMenuList().size(),
            authroties,
            account.getCreatedAt()
        );
    }

}
