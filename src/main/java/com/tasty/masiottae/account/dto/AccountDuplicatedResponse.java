package com.tasty.masiottae.account.dto;

import java.util.Optional;

public record AccountDuplicatedResponse(
        Boolean isDuplicated,
        Optional<String> errorMessage
) {

}
