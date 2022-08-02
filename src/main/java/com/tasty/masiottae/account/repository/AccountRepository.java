package com.tasty.masiottae.account.repository;

import com.tasty.masiottae.account.domain.Account;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Boolean existsByNickName(String nickname);

    Boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);
}