package com.tasty.masiottae.franchise.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_FRANCHISE;

import com.tasty.masiottae.common.exception.custom.NotFoundException;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FranchiseEntityService {

    private final FranchiseRepository franchiseRepository;

    public Franchise findOneFranchiseEntity(Long franchiseId) {
        if (Objects.isNull(franchiseId)) {
            return null;
        }
        return franchiseRepository.findById(franchiseId).orElseThrow(() -> new NotFoundException(
                NOT_FOUND_FRANCHISE.getMessage()));
    }
}
