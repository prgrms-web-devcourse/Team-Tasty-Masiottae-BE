package com.tasty.masiottae.franchise.dto;

import javax.validation.constraints.NotBlank;

public record FranchiseSaveRequest(@NotBlank String name) {

}
