package com.tasty.masiottae.menu.dto;

import com.tasty.masiottae.option.dto.OptionSaveRequest;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public record MenuSaveRequest(@NotNull Long userId, @NotNull Long franchiseId,
                              @NotBlank String title, @NotNull String content,
                              @NotBlank String originalTitle, @PositiveOrZero Integer expectedPrice,
                              @NotNull @Size(min = 1, max = 20) List<OptionSaveRequest> options,
                              @NotNull @Size(min = 1, max = 4) List<Long> tastes) {


}
