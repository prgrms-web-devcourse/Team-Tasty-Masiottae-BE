package com.tasty.masiottae.menu.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

import com.tasty.masiottae.option.dto.OptionSaveRequest;

public record MenuUpdateRequest(
        @NotNull
        Long userId,
        @NotNull
        Long franchiseId,
        @NotBlank
        String title,
        @NotNull        String content,
        @NotBlank
        String originalTitle,
        @NotBlank
        String pictureUrl,
        @PositiveOrZero
        Integer expectedPrice,
        @NotNull @Size(min = 1, max = 20)
        List<@Valid OptionSaveRequest> optionList,
        @NotNull @Size(min = 1, max = 4)
        List<Long> tasteIdList) {

}
