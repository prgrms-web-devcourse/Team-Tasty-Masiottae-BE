package com.tasty.masiottae.security.auth;

import com.tasty.masiottae.account.converter.AccountConverter;
import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.account.service.AccountEntityService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountDetailService implements UserDetailsService {

    private final AccountEntityService accountService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountService.findEntityGraphMenuByEmail(username);
        return new AccountDetail(account);
    }

}
