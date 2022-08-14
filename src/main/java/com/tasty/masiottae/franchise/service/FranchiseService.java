package com.tasty.masiottae.franchise.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_FRANCHISE;

import com.tasty.masiottae.common.aws.AwsS3ImageUploader;
import com.tasty.masiottae.common.exception.custom.NotFoundException;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseConverter franchiseConverter;
    private final AwsS3ImageUploader s3ImageUploader;

    @Transactional
    public FranchiseSaveResponse createFranchise(FranchiseSaveRequest request) {
        String franchiseImageUrl = null;

        if (Objects.nonNull(request.multipartFile())) {
            franchiseImageUrl = s3ImageUploader.uploadFranchiseImage(request.multipartFile());
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

    public Franchise findOneFranchiseEntity(Long franchiseId) {
        if (Objects.isNull(franchiseId)) {
            return null;
        }
        return franchiseRepository.findById(franchiseId).orElseThrow(() -> new NotFoundException(
                NOT_FOUND_FRANCHISE.getMessage()));
    }
}
