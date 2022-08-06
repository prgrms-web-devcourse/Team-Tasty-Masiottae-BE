package com.tasty.masiottae.menu.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.tasty.masiottae.config.QuerydslConfig;
import com.tasty.masiottae.menu.domain.Taste;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QuerydslConfig.class, MenuRepositoryImpl.class})
class TasteRepositoryTest {

    @Autowired
    TasteRepository tasteRepository;

    List<Taste> tastes;

    @BeforeEach
    void init() {
        tastes = List.of(
                tasteRepository.save(Taste.createTaste("매운맛", "#000000")),
                tasteRepository.save(Taste.createTaste("단맛", "#000000")),
                tasteRepository.save(Taste.createTaste("짠맛", "#000000"))
        );
    }

    @Test
    @DisplayName("복수의 아이디로 맛을 조회한다.")
    void findAllByIdsTest() {
        // Given
        List<Long> tasteIds = tastes.stream().map(Taste::getId).toList();

        // When
        List<Taste> result = tasteRepository.findAllByIdIn(tasteIds);

        // Then
        Assertions.assertAll(
                () -> assertThat(result.size()).isEqualTo(tastes.size()),
                () -> assertThat(result).containsAll(tastes)
        );
    }
}