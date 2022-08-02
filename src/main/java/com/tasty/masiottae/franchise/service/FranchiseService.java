package com.tasty.masiottae.franchise.service;

import com.tasty.masiottae.common.util.AwsS3Service;
import com.tasty.masiottae.franchise.FranchiseConverter;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.franchise.dto.FranchiseSaveRequest;
import com.tasty.masiottae.franchise.dto.FranchiseSaveResponse;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseConverter franchiseConverter;
    private final AwsS3Service awsS3Service;

    @Transactional
    public FranchiseSaveResponse createFranchise(FranchiseSaveRequest request) {
        String franchiseImageUrl = null;

        if (Objects.nonNull(request.multipartFile())) {
            franchiseImageUrl = awsS3Service.uploadFranchiseImage(request.multipartFile());
        }
        Franchise franchise = franchiseConverter.toFranchise(request, franchiseImageUrl);
        Franchise savedFranchise = franchiseRepository.save(franchise);

        return franchiseConverter.toFranchiseSaveResponse(savedFranchise);
    }

    public List<FranchiseFindResponse> findAllFranchise() {
        return franchiseRepository.findAll(Sort.by(Direction.ASC, "name")).stream()
                .map(franchiseConverter::toFranchiseFindResponse).collect(
                        Collectors.toList());
    }
}
