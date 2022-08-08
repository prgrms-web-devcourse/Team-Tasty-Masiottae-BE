package com.tasty.masiottae.account.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_ACCOUNT;

import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.repository.AccountRepository;
import com.tasty.masiottae.common.exception.custom.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Component
public class AccountEntityService {

    private final AccountRepository accountRepository;

    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ACCOUNT.getMessage()));
    }


    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ACCOUNT.getMessage()));
    }

    public Account findEntityGraphMenuByEmail(String email) {
        return accountRepository.findEntityGraphMenuByEmail(email)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_ACCOUNT.getMessage()));
    }

}
