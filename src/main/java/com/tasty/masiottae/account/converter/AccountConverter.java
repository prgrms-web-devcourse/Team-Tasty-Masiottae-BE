package com.tasty.masiottae.account.converter;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.dto.AccountFindResponse;
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

}
