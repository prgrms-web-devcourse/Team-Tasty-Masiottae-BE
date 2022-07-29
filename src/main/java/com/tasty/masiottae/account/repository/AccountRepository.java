package com.tasty.masiottae.account.repository;

import com.tasty.masiottae.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}