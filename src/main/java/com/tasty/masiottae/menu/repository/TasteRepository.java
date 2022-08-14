package com.tasty.masiottae.menu.repository;

import com.tasty.masiottae.menu.domain.Taste;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TasteRepository extends JpaRepository<Taste, Long> {

    List<Taste> findAllByIdIn(List<Long> tasteIds);
}
