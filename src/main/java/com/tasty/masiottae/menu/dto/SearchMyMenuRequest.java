package com.tasty.masiottae.menu.dto;

import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

public record SearchMyMenuRequest(

        @NotNull(message = "offset 값은 null 이어서는 안됩니다.")
        @PositiveOrZero(message = "offset은 0이상의 값이어야 합니다.")
        Integer offset,
        @NotNull(message = "limit 값은 null 이어서는 안됩니다.")
        @Positive(message = "limit은 0보다 커야합니다.")
        Integer limit,
        String keyword, String sort, List<Long> tasteIdList) {

}
