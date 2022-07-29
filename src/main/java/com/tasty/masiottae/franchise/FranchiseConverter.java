package com.tasty.masiottae.franchise;

import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.franchise.dto.FranchiseSaveRequest;
import com.tasty.masiottae.franchise.dto.FranchiseSaveResponse;
import org.springframework.stereotype.Component;

@Component
public class FranchiseConverter {

    public FranchiseSaveResponse toFranchiseSaveResponse(Franchise franchise) {
        return new FranchiseSaveResponse(franchise.getId());
    }

    public Franchise toFranchise(FranchiseSaveRequest request, String logoImg) {
        return Franchise.createFranchise(
                request.name(),
                logoImg
        );
    }

    public FranchiseFindResponse toFranchiseFindResponse(Franchise franchise) {
        return new FranchiseFindResponse(franchise.getId(), franchise.getLogoUrl(),
                franchise.getName());
    }
}
