package com.tasty.masiottae.account.repository;

import com.tasty.masiottae.account.domain.Account;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Boolean existsByNickName(String nickname);

    Boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);

    @EntityGraph(attributePaths = {"menuList"})
    Optional<Account> findEntityGraphMenuByEmail(String email);
}