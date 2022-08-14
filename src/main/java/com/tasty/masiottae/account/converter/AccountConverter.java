package com.tasty.masiottae.account.converter;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.dto.AccountCreateRequest;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.security.jwt.JwtAccessToken;
import com.tasty.masiottae.security.jwt.JwtRefreshToken;
import com.tasty.masiottae.security.jwt.JwtToken;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    public Account fromAccountCreateRequest(
        AccountCreateRequest accountCreateRequest,
        MultipartFile image) {
        return Account.createAccount(accountCreateRequest.email(),
            accountCreateRequest.password(),
            accountCreateRequest.nickName(),
            null,
            accountCreateRequest.snsAccount());
    }

    public JwtToken toJwtToken(
        JwtAccessToken jwtAccessToken,
        JwtRefreshToken jwtRefreshToken) {
        return new JwtToken(jwtAccessToken, jwtRefreshToken);
    }


}
