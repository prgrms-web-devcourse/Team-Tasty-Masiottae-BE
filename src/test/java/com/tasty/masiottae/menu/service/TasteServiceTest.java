package com.tasty.masiottae.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.tasty.masiottae.config.QuerydslConfig;
import com.tasty.masiottae.menu.TasteConverter;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.menu.dto.TasteSaveRequest;
import com.tasty.masiottae.menu.dto.TasteSaveResponse;
import com.tasty.masiottae.menu.repository.TasteRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({TasteService.class, TasteConverter.class, QuerydslConfig.class})
class TasteServiceTest {

    @Autowired
    TasteRepository tasteRepository;
    @Autowired
    TasteService tasteService;

    @Test
    @DisplayName("맛을 저장한다.")
    void createTasteTest() {
        // Given
        String tasteName = "매운맛";
        String tasteColor = "####";
        TasteSaveRequest request = new TasteSaveRequest(tasteName, tasteColor);

        // When
        TasteSaveResponse response = tasteService.createTaste(request);

        // Then
        Taste findTaste = tasteRepository.findById(response.tasteId()).get();

        assertAll(
                () -> assertThat(findTaste.getId()).isEqualTo(response.tasteId()),
                () -> assertThat(findTaste.getTasteName()).isEqualTo(tasteName),
                () -> assertThat(findTaste.getTasteColor()).isEqualTo(tasteColor)
        );
    }

    @Test
    @DisplayName("전체 맛을 조회한다.")
    void findAllTasteTest() {
        // Given
        List<Taste> tasteList = List.of(
                tasteRepository.save(Taste.createTaste("매운맛", "####")),
                tasteRepository.save(Taste.createTaste("단맛", "####")),
                tasteRepository.save(Taste.createTaste("쓴맛", "####"))
        );

        tasteRepository.saveAll(tasteList);

        // When
        List<TasteFindResponse> allTaste = tasteService.findAllTaste();

        // Then
        assertThat(allTaste.size()).isEqualTo(3);
        assertThat(allTaste).extracting("tasteId").containsExactly(tasteList.stream().map(
                Taste::getId).toArray());
    }
}