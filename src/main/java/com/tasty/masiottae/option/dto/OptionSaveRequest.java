package com.tasty.masiottae.option.dto;

import javax.validation.constraints.NotBlank;

public record OptionSaveRequest(@NotBlank String name, @NotBlank String description) {

}
