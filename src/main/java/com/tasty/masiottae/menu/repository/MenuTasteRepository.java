package com.tasty.masiottae.menu.repository;

import com.tasty.masiottae.menu.domain.MenuTaste;
import com.tasty.masiottae.menu.domain.MenuTasteId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuTasteRepository extends JpaRepository<MenuTaste, MenuTasteId> {

}
