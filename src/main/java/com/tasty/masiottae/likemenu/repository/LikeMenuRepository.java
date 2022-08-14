package com.tasty.masiottae.likemenu.repository;


import com.tasty.masiottae.account.domain.Account;
import com.tasty.masiottae.likemenu.domain.LikeMenu;
import com.tasty.masiottae.likemenu.domain.LikeMenuId;
import com.tasty.masiottae.menu.domain.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface LikeMenuRepository extends JpaRepository<LikeMenu, LikeMenuId> {
    boolean existsByAccountAndMenu(
            Account account, Menu menu);

    @EntityGraph(attributePaths = {"menu"})
    Page<LikeMenu> findEntityGraphNByAccount(@Param("account") Account account, Pageable pageable);
}
