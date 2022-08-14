package com.tasty.masiottae.franchise.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tasty.masiottae.config.S3TestConfig;
import com.tasty.masiottae.franchise.domain.Franchise;
import com.tasty.masiottae.franchise.dto.FranchiseFindResponse;
import com.tasty.masiottae.franchise.dto.FranchiseSaveRequest;
import com.tasty.masiottae.franchise.dto.FranchiseSaveResponse;
import com.tasty.masiottae.franchise.repository.FranchiseRepository;
import io.findify.s3mock.S3Mock;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(S3TestConfig.class)
class FranchiseServiceTest {

    @Autowired
    FranchiseRepository franchiseRepository;
    @Autowired
    FranchiseService franchiseService;
    @Autowired
    S3Mock s3Mock;

    @Test
    @DisplayName("프랜차이즈 생성 테스트")
    void testCreateFranchise() {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("file", "image.png", "img/png",
            "Hello".getBytes());
        FranchiseSaveRequest franchiseSaveRequest = new FranchiseSaveRequest("스타벅스", multipartFile);

        // when
        FranchiseSaveResponse createdFranchise = franchiseService.createFranchise(
            franchiseSaveRequest);

        // then
        Franchise findFranchise = franchiseRepository.findById(createdFranchise.franchiseId())
            .get();
        assertAll(
            () -> assertThat(findFranchise.getId()).isEqualTo(createdFranchise.franchiseId()),
            () -> assertThat(findFranchise.getLogoUrl()).startsWith(
                "http://localhost:8001/masiottae-image-bucket/franchise/"),
            () -> assertThat(findFranchise.getName()).isEqualTo("스타벅스")
        );
    }

    @Test
    @DisplayName("프랜차이즈 전체 조회 테스트")
    void testFindAllFranchiseTest() {
        // given
        Franchise franchise1 = Franchise.createFranchise("franchise1", "logo.png");
        Franchise franchise2 = Franchise.createFranchise("franchise2", "logo2.png");
        franchiseRepository.saveAll(List.of(franchise1, franchise2));

        // when
        List<FranchiseFindResponse> allFranchise = franchiseService.findAllFranchise();

        // then
        assertThat(allFranchise).hasSize(2);
    }
}
