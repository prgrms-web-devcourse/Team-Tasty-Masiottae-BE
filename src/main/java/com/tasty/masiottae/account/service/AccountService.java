package com.tasty.masiottae.account.service;

import com.tasty.masiottae.account.dto.AccountDuplicatedResponse;
import com.tasty.masiottae.account.repository.AccountRepository;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Getter
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

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

}
