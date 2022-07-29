package com.tasty.masiottae.menu.repository;

import com.tasty.masiottae.menu.domain.Taste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TasteRepository extends JpaRepository<Taste, Long> {

}
