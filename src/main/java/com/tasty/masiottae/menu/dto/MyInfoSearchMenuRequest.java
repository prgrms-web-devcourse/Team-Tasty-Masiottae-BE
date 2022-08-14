package com.tasty.masiottae.menu.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

public record MyInfoSearchMenuRequest(

        @NotNull(message = "accountId 값은 null 이어서는 안됩니다.") Long accountId,
        @NotNull(message = "offset 값은 null 이어서는 안됩니다.")
        @PositiveOrZero(message = "offset은 0이상의 값이어야 합니다.")
        Integer offset,
        @NotNull(message = "limit 값은 null 이어서는 안됩니다.")
        @Positive(message = "limit은 0보다 커야합니다.")
        Integer limit,
        String keyword, @NotBlank(message = "정렬 조건을 지정해주세요.") String sort,
        @Size(max = 4, message = "taste 검색 조건 지정은 최대 4개까지 가능합니다.") List<Long> tasteIdList) {

}
