package com.tasty.masiottae.menu.service;

import static com.tasty.masiottae.common.exception.ErrorMessage.NOT_FOUND_SOME_TASTE;

import com.amazonaws.util.CollectionUtils;
import com.tasty.masiottae.common.exception.custom.NotFoundException;
import com.tasty.masiottae.menu.TasteConverter;
import com.tasty.masiottae.menu.domain.Taste;
import com.tasty.masiottae.menu.dto.TasteFindResponse;
import com.tasty.masiottae.menu.dto.TasteSaveRequest;
import com.tasty.masiottae.menu.dto.TasteSaveResponse;
import com.tasty.masiottae.menu.repository.TasteRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TasteService {

    private final TasteRepository tasteRepository;
    private final TasteConverter tasteConverter;

    public List<TasteFindResponse> findAllTaste() {
        return tasteRepository.findAll().stream()
                .map(tasteConverter::toTasteFindResponse)
                .toList();
    }

    @Transactional
    public TasteSaveResponse createTaste(TasteSaveRequest request) {
        Long tasteId = tasteRepository.save(tasteConverter.toTaste(request)).getId();
        return new TasteSaveResponse(tasteId);
    }

    public List<Taste> findTasteByIds(List<Long> tasteIds) {
        if (CollectionUtils.isNullOrEmpty(tasteIds)) {
            return Collections.emptyList();
        }

        List<Taste> tastes = tasteRepository.findAllByIdIn(tasteIds);
        validateTastesSize(tasteIds, tastes);
        return tastes;
    }

    public List<Taste> saveAll(List<Taste> tasteList) {
        return tasteRepository.saveAll(tasteList);
    }

    private void validateTastesSize(List<Long> tasteIds, List<Taste> tastes) {
        if (tasteIds.size() != tastes.size()) {
            throw new NotFoundException(NOT_FOUND_SOME_TASTE.getMessage());
        }
    }
}
