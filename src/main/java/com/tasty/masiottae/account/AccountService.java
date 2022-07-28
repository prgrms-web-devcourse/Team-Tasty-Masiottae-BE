package com.tasty.masiottae.account;

import com.tasty.masiottae.account.domain.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow();
    }
}