package com.tasty.masiottae.franchise.repository;

import com.tasty.masiottae.franchise.domain.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

}
