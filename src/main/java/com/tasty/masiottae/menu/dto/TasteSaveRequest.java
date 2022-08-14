package com.tasty.masiottae.menu.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record TasteSaveRequest(
        @NotBlank(message = "공백 또는 널 값을 허용하지 않습니다.")
        String name,
        @NotBlank(message = "공백 또는 널 값을 허용하지 않습니다.")
        @Pattern(regexp = "(#?([a-fA-F0-9]{3})([a-fA-F0-9]{3}?))", message = "올바른 색상 코드 형식이 아닙니다.")
        String color) {
}
