package com.tasty.masiottae.account.service;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.dto.AccountCreateRequest;
import com.tasty.masiottae.account.dto.AccountDuplicatedResponse;
import com.tasty.masiottae.account.dto.AccountFindResponse;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.security.auth.AccountDetail;
import com.tasty.masiottae.security.jwt.JwtToken;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import com.tasty.masiottae.security.jwt.JwtTokenResponse;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenResponse save(AccountCreateRequest accountCreateRequest) {
        Account account = Account.createAccount(accountCreateRequest.email(),
                accountCreateRequest.password(), accountCreateRequest.nickname(),
                accountCreateRequest.imgUrl());
        AccountFindResponse accountFindResponse = new AccountFindResponse(account.getId(),
                account.getNickname(), account.getImageUrl(), account.getEmail(),
                account.getCreatedAt(), account.getMenuList().size());
        account.encryptPassword(account.getPassword(), passwordEncoder);

        accountRepository.save(account);

        AccountDetail accountDetail = new AccountDetail(account);
        JwtToken jwtToken = jwtTokenProvider.generatedAccountToken(accountDetail);
        return new JwtTokenResponse(jwtToken, accountFindResponse);
    }

    public AccountDuplicatedResponse duplicateCheckNickname(String nickname) {
        boolean isNickname = accountRepository.existsByNickname(nickname);
        Optional<String> errorMessage = Optional.empty();
        if (isNickname) {
            errorMessage = Optional.of("에러메세지");
        }

        return new AccountDuplicatedResponse(isNickname, errorMessage);
    }

    public AccountDuplicatedResponse duplicateCheckEmail(String email) {
        boolean isEmail = accountRepository.existsByEmail(email);
        Optional<String> errorMessage = Optional.empty();
        if (isEmail) {
            errorMessage = Optional.of("에러메세지");
        }

        return new AccountDuplicatedResponse(isEmail, errorMessage);
    }

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow();
    }

}
