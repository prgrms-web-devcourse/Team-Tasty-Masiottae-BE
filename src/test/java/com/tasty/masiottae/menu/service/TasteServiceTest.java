package com.tasty.masiottae.menu.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.tasty.masiottae.common.exception.custom.NotFoundException;
import com.tasty.masiottae.menu.TasteConverter;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.menu.dto.TasteSaveRequest;
import com.tasty.masiottae.menu.dto.TasteSaveResponse;
import com.tasty.masiottae.menu.repository.TasteRepository;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TasteServiceTest {

    @Mock
    TasteRepository tasteRepository;
    @Mock
    TasteConverter tasteConverter;
    @InjectMocks
    TasteService tasteService;

    @Test
    @DisplayName("Taste를 저장한다.")
    void createTasteTest() {
        // Given
        String name = "매운맛";
        String color = "#000000";
        Long id = 1L;
        TasteSaveRequest request = new TasteSaveRequest(name, color);
        Taste taste = Taste.createTaste(name, color);
        ReflectionTestUtils.setField(taste, "id", id);

        given(tasteConverter.toTaste(request)).willReturn(taste);
        given(tasteRepository.save(taste)).willReturn(taste);

        // When
        TasteSaveResponse response = tasteService.createTaste(request);

        // Then
        assertThat(response.tasteId()).isEqualTo(id);
        then(tasteConverter).should().toTaste(request);
        then(tasteRepository).should().save(taste);
    }

    @Test
    @DisplayName("이미 존재하는 이름으로 Taste를 생성하려하면 예외가 발생한다.")
    void createTasteExistsNameTest() {
        // Given
        String duplicatedName = "매운맛";
        String color = "#000000";
        TasteSaveRequest request = new TasteSaveRequest(duplicatedName, color);
        Taste taste = Taste.createTaste(duplicatedName, color);

        given(tasteConverter.toTaste(request)).willReturn(taste);
        given(tasteRepository.save(taste)).willAnswer(invocation -> {
            throw new SQLIntegrityConstraintViolationException();
        });

        // When // Then
        assertThatThrownBy(() -> tasteService.createTaste(request)).isExactlyInstanceOf(
                SQLIntegrityConstraintViolationException.class);
        then(tasteConverter).should().toTaste(request);
        then(tasteRepository).should().save(taste);
    }

    @Test
    @DisplayName("전체 Taste를 조회한다.")
    void findAllTasteTest() {
        // Given
        List<Taste> savedTastes = List.of(
                Taste.createTaste("매운맛", "#FFFFFF"),
                Taste.createTaste("단맛", "#FFFFFF"),
                Taste.createTaste("쓴맛", "#FFFFFF")
        );
        given(tasteRepository.findAll()).willReturn(savedTastes);

        // When
        List<TasteFindResponse> findTastes = tasteService.findAllTaste();

        // Then
        Assertions.assertAll(
                () -> assertThat(findTastes.size()).isEqualTo(savedTastes.size()),
                () -> assertThat(findTastes).usingRecursiveFieldByFieldElementComparator()
                        .containsAll(findTastes)
        );
        then(tasteRepository).should().findAll();
    }

    @Test
    @DisplayName("아이디 리스트를 전달하여 해당하는 Taste들을  조회한다.")
    void findTasteByIdsTest() {
        // Given
        List<Long> tasteIds = List.of(1L, 2L, 3L);
        List<Taste> savedTastes = List.of(
                Taste.createTaste("매운맛", "#FFFFFF"),
                Taste.createTaste("단맛", "#FFFFFF"),
                Taste.createTaste("쓴맛", "#FFFFFF")
        );
        given(tasteRepository.findAllByIdIn(tasteIds)).willReturn(savedTastes);

        // When
        List<Taste> tasteByIds = tasteService.findTasteByIds(tasteIds);

        // Then
        Assertions.assertAll(
                () -> assertThat(tasteByIds.size()).isEqualTo(3),
                () -> assertThat(tasteByIds).usingRecursiveFieldByFieldElementComparator()
                        .containsAll(savedTastes)
        );
        then(tasteRepository).should().findAllByIdIn(tasteIds);
    }

    @Test
    @DisplayName("null 또는 비어있는 tasteId 리스트를 전달하여 Taste들을 조회하면 빈 리스트가 반환 된다.")
    void findTasteByIdsEmptyTest() {
        // Given
        List<Long> emptyTasteIds = Collections.emptyList();

        // When
        List<Taste> tasteByIds = tasteService.findTasteByIds(emptyTasteIds);

        // Then
        assertThat(tasteByIds).isEmpty();
        then(tasteRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("존재하지 않는 아이디가 포함된 리스트로 Taste를 조회하려 하면 예외가 발생한다.")
    void findTasteByIdsContainNotFoundIdTest() {
        // Given
        Long notExistsId = -1L;
        List<Long> tasteIds = List.of(notExistsId, 2L, 3L);
        List<Taste> retTastes = List.of(
                Taste.createTaste("매운맛", "#FFFFFF"),
                Taste.createTaste("단맛", "#FFFFFF")
        );
        given(tasteRepository.findAllByIdIn(tasteIds)).willReturn(retTastes);

        // When // Then
        assertThatThrownBy(() -> tasteService.findTasteByIds(tasteIds))
                .isExactlyInstanceOf(NotFoundException.class);
        then(tasteRepository).should().findAllByIdIn(tasteIds);
    }
}