package com.tasty.masiottae.menu.repository;

import com.tasty.masiottae.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

}
