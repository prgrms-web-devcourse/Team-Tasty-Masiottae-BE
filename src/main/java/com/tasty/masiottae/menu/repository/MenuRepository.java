package com.tasty.masiottae.menu.repository;

import com.tasty.masiottae.menu.domain.Menu;
import java.util.Optional;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<Menu, Long>, MenuRepositoryCustom {

    @Query("select distinct m from Menu m left join fetch m.account a "
        + "left join fetch m.franchise f "
        + "left join fetch m.likeMenuList "
        + "where m.id = :menuId")
    Optional<Menu> findByIdFetch(@Param("menuId") Long menuId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "3000"))
    @Query("select m from Menu m where m.id = :menuId")
    Optional<Menu> findByIdForUpdate(@Param("menuId") Long menuId);
}
